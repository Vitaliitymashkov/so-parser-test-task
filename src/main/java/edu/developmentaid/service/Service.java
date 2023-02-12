package edu.developmentaid.service;

import edu.developmentaid.StackOverflowAPI;
import edu.developmentaid.model.User;
import edu.developmentaid.model.UserList;
import edu.developmentaid.model.UserTags;
import edu.developmentaid.model.UserTagsItems;
import edu.developmentaid.util.Config;
import edu.developmentaid.util.Extractor;
import edu.developmentaid.util.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.isNull;

public class Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
    private static final String API_KEY_ENV_NAME = Config.getProperty("STACKOVERFLOW_API_KEY_SYSTEM_VARIABLE_NAME", "key");
    private static final String API_KEY = System.getenv(API_KEY_ENV_NAME);
    private static final Set<String> ALLOWED_LOCATIONS = Extractor.getElementsCollection(Config.getProperty("ALLOWED_LOCATIONS", "Romania,Moldova"));
    private static final Set<String> REQUIRED_TAGS = Extractor.getElementsCollection(Config.getProperty("REQUIRED_TAGS", "java,.net,docker,c#"));


    private static final long MILLIS_BETWEEN_CALLS = Long.parseLong(Config.getProperty("MILLIS_BETWEEN_CALLS", "5000"));
    private static final long MILLIS_FOR_THROTTLING_WAIT = Long.parseLong(Config.getProperty("MILLIS_FOR_THROTTLING_WAIT", "60000"));
    private static final int PAGE_SIZE_FOR_USERS = Integer.parseInt(Config.getProperty("PAGE_SIZE_FOR_USERS", "100"));
    private static final int PAGE_SIZE_FOR_TAGS = Integer.parseInt(Config.getProperty("PAGE_SIZE_FOR_TAGS", "100"));
    private static final String SITE_NAME = Config.getProperty("SITE_NAME", "stackoverflow");
    public static final String ORDER = Config.getProperty("ORDER", "asc");
    private static final String SORT_BY_FIELD = Config.getProperty("SORT_BY_FIELD", "reputation");
    private static final int MIN_REPUTATION = Integer.parseInt(Config.getProperty("MIN_REPUTATION", "223"));
    private static final int MAX_RETRIES_FOR_USERS = Integer.parseInt(Config.getProperty("MAX_RETRIES_FOR_USERS", "3"));
    private static final int MAX_RETRIES_FOR_TAGS = Integer.parseInt(Config.getProperty("MAX_RETRIES_FOR_TAGS", "3"));
    private static final int FROMDATE_TIME = Extractor.getUnixTime(Config.getProperty("FROMDATE_TIME", "2021/01/01"), 1609459200);
    private static final int TODATE_TIME = Extractor.getUnixTime(Config.getProperty("TODATE_TIME", "NOW"), 1676207324);

    private final List<User> resultingUsersList = new ArrayList<>();
    private final List<UserTagsItems> userTagsItems = new ArrayList<>();
    private UserList usersList = null;
    private int pageNumberForUsersCall = 1;
    private int retryCounterForUsers = 1;
    private boolean retryFlagForUsers = false;
    private UserTags userTags;
    private int pageNumberForTagsCalls = 1;
    private int retryCounterForTags = 1;
    private boolean retryFlagForTags = false;

    public List<User> parse() {
        LOGGER.debug("Starting parsing");
        StackOverflowAPI stackOverflowAPI = StackOverflowAPI.retrofit.create(StackOverflowAPI.class);
        do {
            Call<UserList> call = stackOverflowAPI.getUsers(
                    API_KEY,
                    pageNumberForUsersCall,
                    PAGE_SIZE_FOR_USERS,
                    FROMDATE_TIME,
                    TODATE_TIME,
                    ORDER,
                    SORT_BY_FIELD,
                    SITE_NAME,
                    MIN_REPUTATION
            );

            try {
                Response<UserList> response = executeCallWithPreWaitAndGetUsersListResponse(call);
                usersList = response.body();
                checkIfResponseIsNotSuccessful(call, response);
                if (!retryFlagForUsers) {
                    for (User user : usersList.getItems()) {
                        checkUserForAllowedLocationsRequiredTagsTodo(stackOverflowAPI, response, user);
                    }
                    pageNumberForUsersCall++;
                }
            } catch (IOException e) {
                LOGGER.debug("IO Exception appeared. Retrying... " + e.getMessage());
                retryFlagForUsers = true;
            } catch (NullPointerException e) {
                LOGGER.debug("NPE Exception appeared. Terminating... " + e.getMessage());
                Printer.printToStdout(resultingUsersList);
                throw new RuntimeException(e);
            }
        } while (retryFlagForUsers || usersList.getHas_more().equalsIgnoreCase("true"));

        //TODO - get answers and questions counts and put them into the list
        LOGGER.debug("Finishing parsing");
        return resultingUsersList;
    }

    private void checkIfResponseIsNotSuccessful(Call<UserList> call, Response<UserList> response) throws IOException {
        if (response.code() != 200) {
            String errorBody = "";
            if (response.errorBody() != null) {
                errorBody = response.errorBody().string();
            }
            LOGGER.debug("Bad status code {} from the server. Message: {}. Error body: {}", response.code(), response.message(), errorBody);

            if (response.code() == 400) {
                checkResponseCodeForThrottlingViolation(call, errorBody);
            } else {
                printResultAndThrowRte(errorBody);
            }
        } else {
            retryFlagForUsers = false;
            retryCounterForUsers = 1;
        }
    }

    private void checkUserForAllowedLocationsRequiredTagsTodo(StackOverflowAPI stackOverflowAPI, Response<UserList> response, User user) throws IOException {
        if (containsSubString(ALLOWED_LOCATIONS, user.getLocation())) {
            do {
                Call<UserTags> callTags = stackOverflowAPI.getUserTags(
                        user.getUser_id(),
                        API_KEY,
                        SITE_NAME,
                        pageNumberForTagsCalls,
                        PAGE_SIZE_FOR_TAGS
                );
                LOGGER.debug("Location {} found for user {} - now collecting for tags: {}", user.getLocation(), user.getDisplay_name(), callTags.request().url());
                waiterBeforeCall(retryCounterForTags);
                Response<UserTags> responseTags = callTags.execute();
                checkIfResponseIsNotSuccessful(response, callTags, responseTags);
                if (!retryFlagForTags) {
                    userTags = responseTags.body();
                    Arrays.stream(userTags.getItems()).map(u -> new UserTagsItems(u.getName())).forEach(userTagsItems::add);
                    pageNumberForTagsCalls++;
                }
            } while (retryFlagForTags || userTags.getHas_more().equalsIgnoreCase("true"));

            LOGGER.debug("User tags for name {} and id {} have been fully collected.", user.getDisplay_name(), user.getUser_id());
            User userInThisLocation = new User(user);
            String[] tags = new String[userTagsItems.size()];
            Arrays.setAll(tags, i -> userTagsItems.get(i).getName());
            userInThisLocation.setTags(tags);

            checkIfRequiredTagsArePresentAndAddToResult(user, userInThisLocation, tags);
        }
    }

    private void checkIfRequiredTagsArePresentAndAddToResult(User user, User userInThisLocation, String[] tags) {
        if (containsAllTags(List.of(tags), REQUIRED_TAGS)) {


            //TODO - filter "1 answer"
            resultingUsersList.add(userInThisLocation); //Resulting list is here!
            LOGGER.debug("Added user {}", user.getDisplay_name());
        } else {
            LOGGER.debug("User {} does not have necessary tags :(", userInThisLocation.getDisplay_name());
        }
    }

    private void checkIfResponseIsNotSuccessful(Response<UserList> response, Call<UserTags> callTags, Response<UserTags> responseTags) throws IOException {
        if (responseTags.code() != 200) {
            String errorBody = responseTags.errorBody().string();
            LOGGER.debug("Bad status code {} from the server. Message: {}. Error body: {}", response.code(), response.message(), errorBody);

            if (responseTags.code() == 400) {
                checkResponseForThrottlingViolationForTags(callTags, errorBody);
            } else {
                printResultAndThrowRte(errorBody);
            }
        } else {
            retryFlagForTags = false;
            retryCounterForTags = 1;
        }
    }

    private void checkResponseForThrottlingViolationForTags(Call<UserTags> callTags, String errorBody) {
        if (errorBody.contains("throttle_violation")) {
            try {
                retryFlagForTags = true;
                LOGGER.debug("Retrying tags request #{} for {}s. Waiting for timeout due to {}.", retryCounterForTags, MILLIS_FOR_THROTTLING_WAIT * retryCounterForTags / 1000, errorBody);
                Thread.sleep(MILLIS_FOR_THROTTLING_WAIT * retryCounterForTags);
                retryCounterForTags++;
                if (retryCounterForTags > MAX_RETRIES_FOR_TAGS) {
                    throw new RuntimeException("Max retries counts reached. Terminating on URL " + callTags.request().url());
                }
                LOGGER.debug("Continue processing of Tags URL " + callTags.request().url());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            printResultAndThrowRte(errorBody);
        }
    }

    private void checkResponseCodeForThrottlingViolation(Call<UserList> call, String errorBody) {
        if (errorBody.contains("throttle_violation")) {
            try {
                retryFlagForUsers = true;
                LOGGER.debug("Retrying users request #{} for {}s. Waiting for timeout due to {}.", retryCounterForUsers, MILLIS_FOR_THROTTLING_WAIT * retryCounterForUsers / 1000, errorBody);
                Thread.sleep(MILLIS_FOR_THROTTLING_WAIT * retryCounterForUsers);
                retryCounterForUsers++;
                if (retryCounterForUsers > MAX_RETRIES_FOR_USERS) {
                    throw new RuntimeException("Max retries counts reached. Terminating on URL " + call.request().url());
                }
                LOGGER.debug("Continue processing of Users URL {}", call.request().url());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            printResultAndThrowRte(errorBody);
        }
    }

    private void printResultAndThrowRte(String errorBody) {
        Printer.printToStdout(resultingUsersList);
        throw new RuntimeException("Bad status code from the server: " + errorBody);
    }

    private Response<UserList> executeCallWithPreWaitAndGetUsersListResponse(Call<UserList> call) throws IOException {
        LOGGER.debug(String.valueOf(call.request().url()));
        waiterBeforeCall();
        return call.execute();
    }

    private void waiterBeforeCall() {
        try {
            Thread.sleep(MILLIS_BETWEEN_CALLS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waiterBeforeCall(int retryCounter) {
        try {
            Thread.sleep(MILLIS_BETWEEN_CALLS * retryCounter);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean containsSubString(Collection<String> stringCollection, String str) {
        if (isNull(str)) {
            return false;
        }
        for (String s : stringCollection) {
            if (str.contains(s)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAllTags(Collection<String> stringCollection, Collection<String> requiredTags) {
        if (isNull(stringCollection)) {
            return false;
        }
        List<String> tagsToCheck = new ArrayList<>(requiredTags);
        for (String s : stringCollection) {
            tagsToCheck.remove(s);
        }
        return tagsToCheck.isEmpty();
    }
}
