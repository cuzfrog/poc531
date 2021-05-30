package server.service.auth;

import server.support.annotation.VisibleForTesting;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

final class InHeapAutoRemovalTokenRepository implements TokenRepository {
    private final StripedOldestRemovalLinkedHashMap<String, Token> tokens;

    InHeapAutoRemovalTokenRepository(TimeService timeService) {
        tokens = new StripedOldestRemovalLinkedHashMap<>(token -> token.getExpirationTime().isBefore(timeService.now()), 16);
    }

    @Override
    public void save(Token token) {
        tokens.put(token.getId(), token);
    }

    @Override
    public void remove(String id) {
        tokens.remove(id);
    }

    @Override
    public Token findById(String id) {
        return tokens.get(id);
    }

    /**
     * A thread-safe implementation of Least Recently (put) Used cache.
     * It does not completely remove an obsolete entry, because some buckets may not be touched for a while.
     * But it helps keep obsolete entries under control.
     *
     * The oldest entries should satisfy removal condition eventually, otherwise they will prevent subsequent removal indefinitely.
     *
     * Note: this is still a toy! In heap and no cap posed!
     */
    @VisibleForTesting
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter") // IDEA here is over-vigilant
    static final class StripedOldestRemovalLinkedHashMap<K, V> {
        private final Map<K, V>[] buckets;

        @SuppressWarnings("unchecked")
        StripedOldestRemovalLinkedHashMap(Predicate<V> shouldBeRemoved, int concurrentLevel) {
            if (concurrentLevel < 1) {
                throw new IllegalArgumentException();
            }
            requireNonNull(shouldBeRemoved);
            this.buckets = (Map<K, V>[]) Array.newInstance(Map.class, concurrentLevel);
            for (int i = 0; i < buckets.length; i++) {
                buckets[i] = new LinkedHashMap<>() {
                    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                        return shouldBeRemoved.test(eldest.getValue());
                    }
                };
            }
        }

        void put(K k, V v) {
            Map<K, V> bucket = currentBucket(k);
            synchronized (bucket) {
                bucket.put(k, v);
            }
        }

        void remove(K k) {
            Map<K, V> bucket = currentBucket(k);
            synchronized (bucket) {
                bucket.remove(k);
            }
        }

        V get(K k) {
            Map<K, V> bucket = currentBucket(k);
            synchronized (bucket) {
                return bucket.get(k);
            }
        }

        private Map<K, V> currentBucket(K k) {
            return buckets[(buckets.length - 1) & Objects.hash(k)];
        }
    }
}
