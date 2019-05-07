package bgu.spl.mics.application;

import java.io.Serializable;
import java.util.Objects;

public class Pair<F,S> implements Serializable {
    private final F first;
    private final S second;
    /**
     * Constructor.
     */
    public Pair(F first, S second){
        this.first=first;
        this.second=second;
    }
    public boolean equals(Object o){
        if(!(o instanceof Pair))
         return false;
        Pair<?,?> p=(Pair<?,?>)o;
        return Objects.equals(p.first,first)&&Objects.equals(p.second,second);
    }
    public F getFirst(){
        return first;
    }
    public S getSecond(){
        return second;
    }
}
