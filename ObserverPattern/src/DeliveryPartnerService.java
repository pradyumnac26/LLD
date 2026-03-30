public class DeliveryPartnerService implements OrderObserver{
    @Override
    public void update(String orderId, OrderStatus state) {
        System.out.println("Delivery Partner App: Delivery partner app updated for order "
                + orderId + " with status " + state);
    }
}
