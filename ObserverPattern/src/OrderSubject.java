public interface OrderSubject {
    void addObservers(OrderObserver observer);
     void removeObserver(OrderObserver observer) ;
    void notifyAllObservers();
}
