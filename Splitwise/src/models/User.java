package models;

import java.util.UUID;

public class User {
    private String id;
    private String name ;
    private String email ;
    private final BalanceSheet balanceSheet;

    public User(String name, String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.balanceSheet = new BalanceSheet(this);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public BalanceSheet getBalanceSheet() {
        return balanceSheet;
    }
}
