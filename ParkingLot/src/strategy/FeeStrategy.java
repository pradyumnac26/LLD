package strategy;

import model.Ticket;

public interface FeeStrategy {
    double calculateFee(Ticket ticket);
}