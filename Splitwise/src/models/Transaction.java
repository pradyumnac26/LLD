package models;

import java.util.UUID;

public class Transaction {
    private String id;
    private User from ;
    private User to ;
    private final double amount;

    public Transaction(User from, User to, double amount) {
        this.id = UUID.randomUUID().toString();
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
    @Override
    public String toString() {
        return from.getName() + " should pay " + to.getName() + " $" + String.format("%.2f", amount);
    }


}
