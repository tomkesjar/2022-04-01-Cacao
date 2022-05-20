package common.messages;

import java.io.Serializable;

public class Pair<K extends Object, V extends Object> extends Object implements Serializable {
    private K key;
    private V value;

    public Pair(K k, V v) {
        this.key = k;
        this.value = v;
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
        return "Pair(" + key + ", " + value +')';
    }
}
