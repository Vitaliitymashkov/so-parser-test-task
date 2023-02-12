package edu.developmentaid.model;

import static java.util.Objects.isNull;

public class User {
    private long user_id;
    private String display_name;
    private String location;
    private int answer_count;
    private int question_count;
    private String[] tags;
    private String link;
    private String profile_image;

    public User(User user) {
        this.user_id = user.user_id;
        this.display_name = user.display_name;
        this.location = user.location;
        this.answer_count = user.answer_count;
        this.question_count = user.question_count;
        this.tags = user.tags;
        this.link = user.link;
        this.profile_image = user.profile_image;
    }

    public User(long user_id, String display_name, String location, int answer_count, int question_count, String[] tags, String link, String profile_image) {
        this.user_id = user_id;
        this.display_name = display_name;
        this.location = location;
        this.answer_count = answer_count;
        this.question_count = question_count;
        this.tags = tags;
        this.link = link;
        this.profile_image = profile_image;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }


    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAnswer_count(int answer_count) {
        this.answer_count = answer_count;
    }

    public void setQuestion_count(int question_count) {
        this.question_count = question_count;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
    public String getDisplay_name() {
        return display_name;
    }

    public String getLocation() {
        return location;
    }

    public int getAnswer_count() {
        return answer_count;
    }

    public int getQuestion_count() {
        return question_count;
    }

    public String getComaSeparatedTags() {
        return isNull(tags) ? "null" : String.join(",", tags);
    }

    public String getLink() {
        return link;
    }

    public String getProfile_image() {
        return profile_image;
    }
}
