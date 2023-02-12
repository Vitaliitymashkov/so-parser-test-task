package edu.developmentaid.model;

public class UserList {
        private User[] items;

        private String  has_more;

    public UserList(User[] items, String has_more) {
        this.items = items;
        this.has_more = has_more;
    }

    public User[] getItems() {
        return items;
    }

    public void setItems(User[] items) {
        this.items = items;
    }

    public String getHas_more() {
        return has_more;
    }

    public void setHas_more(String has_more) {
        this.has_more = has_more;
    }
}
