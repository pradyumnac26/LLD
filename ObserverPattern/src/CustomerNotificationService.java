public class CustomerNotificationService implements OrderObserver {
    @Override
    public void update(String orderId, OrderStatus state) {
        System.out.println("Customer Notification Service: Notifying customer that order "
                + orderId + " is now " + state);
    }
}
