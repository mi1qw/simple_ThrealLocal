public interface ThreadLocal<T> {
    T get();

    void set(T value);

    void remove();
}
