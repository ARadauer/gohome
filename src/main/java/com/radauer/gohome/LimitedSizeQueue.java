package com.radauer.gohome;

import java.util.ArrayList;

/**
 * A queue with a fixed size
 * if size exceeds its size the oldest element will be removed
 */
public class LimitedSizeQueue<K> extends ArrayList<K> {

    private int maxSize;

    public LimitedSizeQueue(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean add(K k) {
        boolean r = super.add(k);
        if (size() > maxSize) {
            removeRange(0, size() - maxSize - 1);
        }
        return r;
    }

    public K getYongest() {
        return get(size() - 1);
    }

    public K getOldest() {
        return get(0);
    }
}
