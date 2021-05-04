package me.lokka30.levelledmobs.misc;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CachedModalList<T extends Comparable<T>> implements Cloneable {
    public CachedModalList(){
        this.items = new TreeSet<>();
        this.listMode = ModalListMode.ALL;
    }

    public CachedModalList(@NotNull Set<T> items){
        this.items = items;
        this.listMode = ModalListMode.ALL;
    }

    public ModalListMode listMode;
    @NotNull
    public Set<T> items;

    public boolean isEnabledInList(final T item) {
        switch (listMode) {
            case WHITELIST:
                return items.contains(item);
            case BLACKLIST:
                return !items.contains(item);
            default: // ALL
            return true;
        }
    }

    public String toString(){
        return this.listMode + ", " + this.items;
    }

    public Object clone(){
        CachedModalList<T> copy = null;
        try {
            copy = (CachedModalList<T>) super.clone();
            copy.items = this.items;
        }
        catch (CloneNotSupportedException ignored) {}

        return copy;
    }
}
