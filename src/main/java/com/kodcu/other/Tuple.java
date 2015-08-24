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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (key != null ? !key.equals(tuple.key) : tuple.key != null) return false;
        return !(value != null ? !value.equals(tuple.value) : tuple.value != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
