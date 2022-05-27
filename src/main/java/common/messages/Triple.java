package common.messages;

import java.io.Serializable;

public class Triple<F extends Object, S extends Object, T extends Object> extends Object implements Serializable {
    private F first;
    private S second;
    private T third;

    public Triple(F f, S s, T t) {
        this.first = f;
        this.second = s;
        this.third = t;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public T getThird() {
        return third;
    }

    public void setThird(T third) {
        this.third = third;
    }

    @Override
    public String toString() {
        return "Pair(" + first + ", " + second + ", " + third +')';
    }



}
