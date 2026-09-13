// License & Terms
//
// This file is part of **Cascara CT**.
//
// **Cascara CT** is free software: you can redistribute
// it and/or modify them without restriction under the terms of
// the MIT License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// MIT License for more details.

package io.github.qishr.cascara.lang.ct.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

import io.github.qishr.cascara.lang.ct.token.CtToken;

public class CtMap extends CtVariable implements Iterable<CtVariable> {
    // private Map<String,CtVariable> map = new HashMap<>();
    private final LinkedHashMap<String,CtVariable> map = new LinkedHashMap<>();

    public CtMap(CtToken token, String name) {
        super(token, name);
    }

    public void set(String name, CtVariable value) {
        map.put(name, value);
    }

    public CtVariable get(String name) {
        return map.get(name);
    }

    public int size() {
        return map.size();
    }

    public CtVariable get(int i) {
        if (i < 0 || i > size()) throw new NoSuchElementException();
        return map.sequencedValues().toArray(new CtVariable[]{})[i];
    }

    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<CtVariable> iterator() {
        return map.sequencedValues().iterator();
    }

    static class SequenceIterator<T> implements Iterator<CtVariable> {
        CtMap list;
        int currentIndex = 0;

        // initialize pointer to head of the list for iteration
        public SequenceIterator(CtMap list) {
            this.list = list;
        }

        // returns false if next element does not exist
        public boolean hasNext() {
            return currentIndex < list.size();
        }

        // return current data and update pointer
        public CtVariable next() {
            CtVariable data = list.get(currentIndex++);
            return data;
        }

        // implement if needed
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
