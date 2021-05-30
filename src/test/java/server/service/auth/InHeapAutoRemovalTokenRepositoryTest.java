package server.service.auth;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import server.test.support.annotation.SlowTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class InHeapAutoRemovalTokenRepositoryTest {
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    @AfterAll
    void teardown() {
        executorService.shutdown();
    }

    @Test
    void oldestEntryRemoved() {
        InHeapAutoRemovalTokenRepository.StripedOldestRemovalLinkedHashMap<Integer, Boolean> cache =
                new InHeapAutoRemovalTokenRepository.StripedOldestRemovalLinkedHashMap<>(v -> v, 1);
        cache.put(1, true);
        cache.put(2, true);
        cache.put(3, false);
        cache.put(4, true);
        assertThat(cache.get(1)).isNull();
        assertThat(cache.get(2)).isNull();
        assertThat(cache.get(3)).isNotNull();
        assertThat(cache.get(4)).isNotNull();
    }

    @SlowTest
    @Test
    void oldEntryEventuallyRemoved() {
        InHeapAutoRemovalTokenRepository.StripedOldestRemovalLinkedHashMap<Integer, Long> cache =
                new InHeapAutoRemovalTokenRepository.StripedOldestRemovalLinkedHashMap<>(timestamp -> timestamp < System.currentTimeMillis(), 4);
        executorService.submit(() -> {
            for (int i = 0; i < 50000; i++) {
                cache.put(i, System.currentTimeMillis() + 100);
            }
        });
        executorService.submit(() -> {
            for (int i = 50000; i < 100000; i++) {
                cache.put(i, System.currentTimeMillis() + 100);
            }
        });

        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).until(() -> {
            for (int i = -100000; i < 0; i++) { // try to make all buckets touched
                cache.put(i, 0L);
            }

            int jobExistenceCnt = 0;
            for (int i = 0; i < 10; i++) {
                if (cache.get(i) != null) {
                    jobExistenceCnt++;
                }
            }
            return jobExistenceCnt == 0;
        });
    }
}
