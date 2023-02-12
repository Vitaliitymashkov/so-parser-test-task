package edu.developmentaid;

import edu.developmentaid.model.UserList;
import edu.developmentaid.model.UserTags;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StackOverflowAPI {
    @GET("/2.3/users")
    Call<UserList> getUsers(
            @Query("key") String key,
            @Query("page") int page,
            @Query("pagesize") int pagesize,
            @Query("fromdate") int fromdate,
            @Query("todate") int todate,
            @Query("order") String order,
            @Query("sort") String sort,
            @Query("site") String site,
//            @Query("tagged") String tagged,
//            @Query("location") String location,
            @Query("min") int min
    );
/*
    @GET("/2.3/users/{user_id}")
    Call<UserDetails> getUserDetails(
            @Path("user_id") long id,
            @Query("site") String site
    );*/

    //https://api.stackexchange.com/2.3/users/22656/tags?page=20&pagesize=100&order=desc&sort=name&site=stackoverflow
    @GET("/2.3/users/{user_id}/tags")
    Call<UserTags> getUserTags(
            @Path("user_id") long id,
            @Query("key") String key,
            @Query("site") String site,
            @Query("page") int page,
            @Query("pagesize") int pagesize
    );

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.stackexchange.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
