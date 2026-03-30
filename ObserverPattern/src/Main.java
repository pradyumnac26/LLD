public class Main {
    public static void main(String[] args) {
        Order order1 = new Order("ORD-1") ;
        OrderObserver customerService = new CustomerNotificationService();
        OrderObserver deliveryPartnerService = new DeliveryPartnerService();
        OrderObserver analyticsService = new AnalyticsService();
        OrderObserver restaurantService = new RestaurantService();

        order1.addObservers(customerService);
        order1.addObservers(deliveryPartnerService);
        order1.addObservers(analyticsService);
        order1.addObservers(restaurantService);

        order1.setState(OrderStatus.PLACED);
        order1.setState(OrderStatus.PREPARING);
        order1.setState(OrderStatus.OUT_FOR_DELIVERY);
        order1.setState(OrderStatus.DELIVERED);



    }
}