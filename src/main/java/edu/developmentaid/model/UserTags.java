package edu.developmentaid.model;

public class UserTags {
    private String  has_more;
    private int quota_max;
    private int quota_remaining;
    private UserTagsItems[] items;

    public UserTags(String has_more, int quota_max, int quota_remaining, UserTagsItems[] items) {
        this.has_more = has_more;
        this.quota_max = quota_max;
        this.quota_remaining = quota_remaining;
        this.items = items;
    }

    public String getHas_more() {
        return has_more;
    }

    public void setHas_more(String has_more) {
        this.has_more = has_more;
    }

    public UserTagsItems[] getItems() {
        return items;
    }

    public void setItems(UserTagsItems[] items) {
        this.items = items;
    }

    public int getQuota_max() {
        return quota_max;
    }

    public void setQuota_max(int quota_max) {
        this.quota_max = quota_max;
    }

    public int getQuota_remaining() {
        return quota_remaining;
    }

    public void setQuota_remaining(int quota_remaining) {
        this.quota_remaining = quota_remaining;
    }
}
