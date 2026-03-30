import java.sql.SQLOutput;

public class AnalyticsService implements OrderObserver {
    @Override
    public void update(String orderId, OrderStatus state) {
        System.out.println("Analytics Service: Tracking status change for order "
                + orderId + " -> " + state);
    }
}
