package models;

import java.util.UUID;

public class User {
    public String id;
    public String name;
    public String email;

    public User(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name ;
        this.email = email ;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
