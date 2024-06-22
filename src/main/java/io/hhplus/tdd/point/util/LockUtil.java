package io.hhplus.tdd.point.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockUtil {

    private Map<Object, Lock> lockMap = new ConcurrentHashMap<>();
    private Lock lock;

    public void lock(Object id) {
        lock = lockMap.computeIfAbsent(id, key -> new ReentrantLock());
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
