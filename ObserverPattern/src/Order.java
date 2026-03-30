import java.util.ArrayList;
import java.util.List;

public class Order implements OrderSubject {
    private String orderId;
    List<OrderObserver> observers;
    OrderStatus state;

    public Order(String orderId){
        this.orderId = orderId;
        this.observers = new ArrayList<>();
        this.state = OrderStatus.PLACED;

    }
    @Override
    public void addObservers(OrderObserver observer) {
        observers.add(observer);

    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);

    }

    @Override
    public void notifyAllObservers() {
        for (OrderObserver observer : observers) {
            observer.update(orderId, state);
        }

    }

    public void setState(OrderStatus state) {
        this.state = state;
        System.out.println("\nOrder " + orderId + " changed to: " + state);
        notifyAllObservers();

    }
}
