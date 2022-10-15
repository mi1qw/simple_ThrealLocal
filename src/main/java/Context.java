import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


public class Context {
    private static final Context CONTEXT = new Context();
    private static Map<Thread, Map<Local, Local>> map =
            Collections.synchronizedMap(new WeakHashMap<>());

    public static Context inst() {
        return CONTEXT;
    }

    public Local get(final Local local) {
        Map<Local, Local> localMap = map.get(Thread.currentThread());
        if (localMap == null) {
            return null;
        }
        return localMap.get(local);
    }


    public void set(final Local<?> local) {
        map.compute(Thread.currentThread(), (t, map) -> {
            if (map == null) {
                Map<Local, Local> newMap = new HashMap<>();
                newMap.put(local, local);
                return newMap;
            }
            map.put(local, local);
            return map;
        });
    }


    public void remove(final Local<?> local) {
        map.computeIfPresent(Thread.currentThread(), (t, map) -> {
            map.remove(local);
            return map;
        });
    }

    public Map<Thread, Map<Local, Local>> getMap() {
        return map;
    }
}
