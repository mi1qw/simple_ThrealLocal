import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LocalTest {

    @Test
    void setAndGet() {
        Local<Integer> localInt = new Local<>();
        localInt.set(1);
        Local<String> localStr = new Local<>();
        localStr.set("smth");
        assertThat(localInt.get()).isEqualTo(1);
        assertThat(localStr.get()).isEqualTo("smth");
    }

    @Test
    void setNull() {
        Local<Integer> localNull = new Local<>();
        Local<String> localStr = new Local<>();
        localStr.set("smth");
        assertThat(localNull.get()).isNull();
        assertThat(localStr.get()).isEqualTo("smth");
    }

    @Test
    void removeNullLocal() {
        Local<Integer> localNull = new Local<>();
        assertThatNoException().isThrownBy(localNull::remove);
    }

    @Test
    void remove() {
        Local<Integer> localInt = new Local<>();
        localInt.set(1);
        Local<String> localStr = new Local<>();
        localStr.set("smth");
        localInt.remove();
        assertThat(localInt.get()).isNull();
        localStr.remove();
        assertThat(localStr.get()).isNull();
    }

    @Test
    void manyThreads() throws InterruptedException {
        for (int i = 0; i < 7; i++) {
            int finalI = i;
            new Thread(() -> {
                Local<Integer> localInt = new Local<>();
                localInt.set(finalI);
                Local<String> localStr = new Local<>();
                localStr.set(finalI + " string");
                try {
                    Thread.sleep(1000 * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()
                                   + "  " + localInt.getValue());
                System.out.println(Thread.currentThread().getName()
                                   + "  " + localStr.getValue());
                assertThat(localInt.get()).isEqualTo(finalI);
                assertThat(localStr.get()).isEqualTo(finalI + " string");
            }, "ThreadName_" + i).start();
        }
        Thread.sleep(1000 * 5);
    }

    @Test
    @Order(1)
    void manyThreadsGC() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    Local<Integer> localInt = new Local<>();
                    localInt.set(finalI);
                    Local<String> localStr = new Local<>();
                    localStr.set(finalI + " string");
                    try {
                        Thread.sleep(random.nextInt(5000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    assertThat(localInt.get()).isEqualTo(finalI);
                    assertThat(localStr.get()).isEqualTo(finalI + " string");
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }, "ThreadName_" + i).start();
        }
        Map<Thread, Map<Local, Local>> mapThread = Context.inst().getMap();
        while (!mapThread.isEmpty()) {
            System.gc();
            System.out.println("size Threads= " + mapThread.size());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertThat(mapThread.size()).isEqualTo(0);
    }
}
