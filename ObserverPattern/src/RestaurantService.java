public class RestaurantService implements OrderObserver{
    @Override
    public void update(String orderId, OrderStatus state) {
        System.out.println("Restaurant Service: Restaurant sees order "
                + orderId + " is now " + state);
    }
}
