public class Local<T> implements ThreadLocal<T> {
    private T value;

    public Local() {
    }

    @Override
    public T get() {
        Local<T> local = Context.inst().get(this);
        return local == null ? null : local.getValue();
    }

    @Override
    public void set(T value) {
        this.value = value;
        Context.inst().set(this);
    }

    @Override
    public void remove() {
        Context.inst().remove(this);

    }

    public T getValue() {
        return value;
    }
}
