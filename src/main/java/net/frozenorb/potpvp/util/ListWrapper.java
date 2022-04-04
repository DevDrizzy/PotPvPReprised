package net.frozenorb.potpvp.util;

import java.util.LinkedList;
import java.util.List;

public class ListWrapper<T> {
 
    private List<T> backingList;

    public List<T> ensure() {
        return isPresent() ? backingList : (backingList = new LinkedList<T>());
    }

    public boolean isPresent() {
        return backingList != null;
    }
}
