package com.kodcu.other;

/**
 * Created by usta on 21.12.2014.
 */
public class Tuple<K, V> {

    private K key;
    private V value;

    public Tuple() {
    }

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
