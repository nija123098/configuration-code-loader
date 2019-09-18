package com.github.nija123098.configurationcodeloader.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * A utility class for handling listener registration.
 *
 * @param <T> the type to listen to.
 */
public class Register<T> {
    /**
     * The registered listeners for the value T.
     */
    private final List<Consumer<T>> values = new LinkedList<>();

    /**
     * The {@link ReadWriteLock} for locking access to the listener list.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Submit a object to be listened to by the listeners.
     *
     * @param t the object to be listened to.
     */
    protected void submit(T t) {
        this.forEachListener(value -> value.accept(t));
    }

    /**
     * Add a listener.
     *
     * @param value the listener to add.
     */
    public void registerListener(Consumer<T> value) {
        this.lock.writeLock().lock();
        this.values.add(value);
        this.lock.writeLock().unlock();
    }

    /**
     * Remove a listener.
     *
     * @param value the listener to remove.
     */
    public void deregisterListener(Consumer<T> value) {
        this.lock.writeLock().lock();
        this.values.remove(value);
        this.lock.writeLock().unlock();
    }

    /**
     * Iterate over the consumers doing the action.
     *
     * @param forEach the consumer to run on each listener.
     */
    protected void forEachListener(Consumer<Consumer<T>> forEach) {
        this.lock.readLock().lock();
        this.values.forEach(forEach);
        this.lock.readLock().unlock();
    }

    /**
     * Get the {@link ReadWriteLock} used for this instance to ensure concurrency.
     *
     * @return the lock.
     */
    protected ReadWriteLock getLock() {
        return this.lock;
    }
}
