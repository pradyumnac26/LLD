public interface OrderObserver {
    void update(String orderId, OrderStatus state);
}
