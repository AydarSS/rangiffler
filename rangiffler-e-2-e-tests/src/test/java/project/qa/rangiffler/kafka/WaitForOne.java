package project.qa.rangiffler.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class WaitForOne<K, V> {

  private static final long MAX_WAIT_TIME = 5000L;
  private final Map<K, SyncObject<V>> store = new ConcurrentHashMap<>();

  public void provide(K key, V value) {
    store.computeIfAbsent(key, k-> new SyncObject<>(k))
        .provide(value);
  }

  public @Nullable
  V wait(K key) throws InterruptedException {
    SyncObject<V> syncObject = store.computeIfAbsent(key, k-> new SyncObject<>(k));
    return syncObject.latch.await(MAX_WAIT_TIME, TimeUnit.MILLISECONDS)
        ? syncObject.value
        : null;
  }

  private final class SyncObject<V> {

    private final CountDownLatch latch = new CountDownLatch(1);
    private final K key;
    private V value;

    public SyncObject(K key) {
      this.key = key;
    }

    public synchronized void provide(V value) {
      if (latch.getCount() != 0L) {
        this.value = value;
        latch.countDown();
      }
    }
  }
}