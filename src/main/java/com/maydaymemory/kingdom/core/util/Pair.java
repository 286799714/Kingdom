package com.maydaymemory.kingdom.core.util;

import javax.annotation.Nonnull;

/**
 * Represents an ordered pair.
 * */
public class Pair<U,V> implements Comparable<Pair<U,V>>{
    private U former;
    private V latter;

    public Pair(U former, V latter){
        this.former = former;
        this.latter = latter;
    }

    public U getFormer() {
        return former;
    }

    public void setFormer(U former) {
        this.former = former;
    }

    public V getLatter() {
        return latter;
    }

    public void setLatter(V latter) {
        this.latter = latter;
    }

    @Override
    public int compareTo(@Nonnull Pair<U, V> o) {
        if(former.equals(o.former)){
            if(latter.equals(o.latter)){
                return 0;
            }
            else return 1;
        }
        else if(latter.equals(o.latter)){
            return -1;
        }
        else return -2;
    }

    @Override
    public int hashCode(){
        return former.hashCode() * 31 + latter.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Pair){
            Pair<?, ?> p = (Pair<?, ?>) obj;
            boolean flag = true;
            if(former == null){
                if(p.former != null) flag = false;
            }else flag = former.equals(p.former);
            if(!flag) return false;
            if(latter == null){
                if(p.latter != null) flag = false;
            }else flag = latter.equals(p.latter);
            return flag;
        }
        return false;
    }

    @Override
    public String toString(){
        return "{" + former + "," + latter + "}";
    }
}
