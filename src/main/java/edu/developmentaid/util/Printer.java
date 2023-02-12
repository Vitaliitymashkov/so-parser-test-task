package edu.developmentaid.util;

import edu.developmentaid.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class Printer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Printer.class);
    public static void printToStdout(List<User> parsedList) {
        LOGGER.debug(parsedList.isEmpty()
                ? "All requests were successfully executed. The resulting list is empty :("
                : "All requests were successfully executed. Printing the resulting list :)");
        //Print the list of users to STDOUT
        parsedList.forEach(Printer::printValues);
    }

    public static void printValues(User user) {
        //System.out.println(user.getUser_id()); //Note - remove from the final output
        System.out.println(user.getDisplay_name());
        System.out.println(user.getLocation());
        System.out.println(user.getAnswer_count());
        System.out.println(user.getQuestion_count());
        System.out.println(user.getComaSeparatedTags());
        System.out.println(user.getLink());
        System.out.println(user.getProfile_image());
    }
}
