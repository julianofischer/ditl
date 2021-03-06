/*******************************************************************************
 * This file is part of DITL.                                                  *
 *                                                                             *
 * Copyright (C) 2011-2012 John Whitbeck <john@whitbeck.fr>                    *
 *                                                                             *
 * DITL is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU General Public License as published by        *
 * the Free Software Foundation, either version 3 of the License, or           *
 * (at your option) any later version.                                         *
 *                                                                             *
 * DITL is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *
 * GNU General Public License for more details.                                *
 *                                                                             *
 * You should have received a copy of the GNU General Public License           *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.       *
 *******************************************************************************/
package ditl.graphs;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class AdjacencyMap<C extends Couple, T> implements Map<C, T> {

    Map<Integer, Map<Integer, T>> map = new HashMap<Integer, Map<Integer, T>>();
    int size = 0;

    public Map<Integer, T> getStartsWith(Integer i) {
        final Map<Integer, T> from_map = map.get(i);
        if (from_map == null)
            return Collections.emptyMap();
        return from_map;
    }

    public Set<Integer> vertices() {
        final Set<Integer> vertices = new HashSet<Integer>();
        vertices.addAll(map.keySet());
        for (final Map<Integer, T> from_maps : map.values())
            vertices.addAll(from_maps.keySet());
        return Collections.unmodifiableSet(vertices);
    }

    @Override
    public T get(Object key) {
        final Couple c = (Couple) key;
        final Map<Integer, T> from_map = map.get(c.id1());
        if (from_map != null)
            return from_map.get(c.id2());
        return null;
    }

    @Override
    public T put(C c, T obj) {
        if (obj == null)
            throw new NullPointerException();
        final T prev = put(c.id1(), c.id2(), obj);
        if (prev == null)
            size++;
        return prev;
    }

    T put(Integer id1, Integer id2, T obj) {
        T prev = null;
        Map<Integer, T> from_map = map.get(id1);
        if (from_map != null)
            prev = from_map.put(id2, obj);
        else {
            from_map = new HashMap<Integer, T>();
            from_map.put(id2, obj);
            map.put(id1, from_map);
        }
        return prev;
    }

    @Override
    public void putAll(Map<? extends C, ? extends T> m) {
        for (final Map.Entry<? extends C, ? extends T> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    @Override
    public T remove(Object key) {
        final Couple c = (Couple) key;
        final T obj = remove(c.id1(), c.id2());
        size--;
        return obj;
    }

    T remove(Integer id1, Integer id2) {
        final Map<Integer, T> from_map = map.get(id1);
        final T obj = from_map.remove(id2);
        if (from_map.isEmpty())
            map.remove(id1);
        return obj;
    }

    protected abstract C newCouple(Integer id1, Integer id2);

    Iterator<T> valuesIterator() {
        return new ValuesIterator();
    }

    Iterator<C> keysIterator() {
        return new KeysIterator();
    }

    Iterator<Map.Entry<C, T>> entriesIterator() {
        return new EntriesIterator();
    }

    @Override
    public void clear() {
        for (final Map<Integer, T> from_maps : map.values())
            from_maps.clear();
        map.clear();
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        final Couple c = (Couple) key;
        final Map<Integer, T> from_map = map.get(c.id1());
        if (from_map != null)
            return from_map.containsKey(c.id2());
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        final Iterator<T> i = valuesIterator();
        while (i.hasNext()) {
            final T v = i.next();
            if (o == null ? v == null : o.equals(v))
                return true;
        }
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<C, T>> entrySet() {
        return new EntriesView();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<C> keySet() {
        return new KeyView();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Collection<T> values() {
        return new ValuesView();
    }

    private class ValuesView implements Collection<T> {

        @Override
        public boolean add(T e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (final Object o : c)
                if (!containsValue(o))
                    return false;
            return true;
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public Iterator<T> iterator() {
            return valuesIterator();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public Object[] toArray() {
            final Object[] array = new Object[size];
            int j = 0;
            final Iterator<T> i = valuesIterator();
            while (i.hasNext()) {
                array[j] = i.next();
                j++;
            }
            return array;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> E[] toArray(E[] a) {
            return (E[]) toArray();
        }
    }

    private class KeyView implements Set<C> {

        @Override
        public boolean add(C c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends C> couples) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            AdjacencyMap.this.clear();
        }

        @Override
        public boolean contains(Object obj) {
            return map.containsKey(obj);
        }

        @Override
        public boolean containsAll(Collection<?> objects) {
            for (final Object o : objects)
                if (!map.containsKey(o))
                    return false;
            return true;
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public Iterator<C> iterator() {
            return keysIterator();
        }

        @Override
        public boolean remove(Object o) {
            return (AdjacencyMap.this.remove(o) != null);
        }

        @Override
        public boolean removeAll(Collection<?> objects) {
            boolean changed = false;
            for (final Object o : objects)
                changed |= remove(o);
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> objects) {
            boolean changed = false;
            final Iterator<C> i = keysIterator();
            while (i.hasNext()) {
                final C c = i.next();
                if (!objects.contains(c)) {
                    i.remove();
                    changed = true;
                }
            }
            return changed;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Object[] toArray() {
            final Object[] array = new Object[size];
            final Iterator<C> i = keysIterator();
            int j = 0;
            while (i.hasNext()) {
                array[j] = i.next();
                j++;
            }
            return array;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> E[] toArray(E[] arg0) {
            return (E[]) toArray();
        }
    }

    private class EntriesView implements Set<Map.Entry<C, T>> {

        @Override
        public boolean add(java.util.Map.Entry<C, T> e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends java.util.Map.Entry<C, T>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            AdjacencyMap.this.clear();
        }

        @Override
        public boolean contains(Object o) {
            @SuppressWarnings("unchecked")
            final Map.Entry<Couple, T> e = (Map.Entry<Couple, T>) o;
            return map.containsKey(e.getKey());
        }

        @Override
        public boolean containsAll(Collection<?> objects) {
            for (final Object o : objects)
                if (!contains(o))
                    return false;
            return true;
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public Iterator<java.util.Map.Entry<C, T>> iterator() {
            return entriesIterator();
        }

        @Override
        public boolean remove(Object o) {
            @SuppressWarnings("unchecked")
            final Map.Entry<C, T> e = (Map.Entry<C, T>) o;
            return (AdjacencyMap.this.remove(e.getKey()) != null);
        }

        @Override
        public boolean removeAll(Collection<?> objects) {
            boolean changed = false;
            for (final Object o : objects)
                changed |= remove(o);
            return changed;
        }

        @Override
        public boolean retainAll(Collection<?> objects) {
            boolean changed = false;
            final Iterator<C> i = keysIterator();
            while (i.hasNext()) {
                final C c = i.next();
                for (final Object o : objects)
                    if (o != null) {
                        @SuppressWarnings("unchecked")
                        final Map.Entry<C, T> e = (Map.Entry<C, T>) o;
                        if (e.getKey().equals(c)) {
                            i.remove();
                            changed = true;
                        }
                    }
            }
            return changed;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Object[] toArray() {
            final Object[] array = new Object[size];
            final Iterator<Map.Entry<C, T>> i = entriesIterator();
            int j = 0;
            while (i.hasNext()) {
                array[j] = i.next();
                j++;
            }
            return array;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> E[] toArray(E[] a) {
            return (E[]) toArray();
        }

    }

    class ValuesIterator implements Iterator<T> {
        Iterator<Integer> ki = map.keySet().iterator();
        Iterator<Map.Entry<Integer, T>> vi = null;
        Integer id1 = null;
        Integer id2 = null;
        T next = null;

        public ValuesIterator() {
            peek();
        }

        void peek() {
            if (vi == null || !vi.hasNext()) {
                if (!ki.hasNext()) {
                    vi = null;
                    id1 = null;
                    id2 = null;
                    next = null;
                    return;
                }
                id1 = ki.next();
                vi = map.get(id1).entrySet().iterator();
            }
            final Map.Entry<Integer, T> e = vi.next();
            next = e.getValue();
            id2 = e.getKey();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            final T ret = next;
            peek();
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    class KeysIterator implements Iterator<C> {
        Iterator<Integer> ki = map.keySet().iterator();
        Iterator<Integer> vi = null;
        Integer id1 = null;
        Integer id2 = null;

        public KeysIterator() {
            peek();
        }

        void peek() {
            if (vi == null || !vi.hasNext()) {
                if (!ki.hasNext()) {
                    vi = null;
                    id1 = null;
                    id2 = null;
                    return;
                }
                id1 = ki.next();
                vi = map.get(id1).keySet().iterator();
            }
            id2 = vi.next();
        }

        @Override
        public boolean hasNext() {
            return id2 != null;
        }

        @Override
        public C next() {
            final C couple = newCouple(id1, id2);
            peek();
            return couple;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    class EntriesIterator implements Iterator<Map.Entry<C, T>> {
        Iterator<Integer> ki = map.keySet().iterator();
        Iterator<Map.Entry<Integer, T>> vi = null;
        Integer id1 = null;
        Integer id2 = null;
        T next = null;

        public EntriesIterator() {
            peek();
        }

        void peek() {
            if (vi == null || !vi.hasNext()) {
                if (!ki.hasNext()) {
                    vi = null;
                    id1 = null;
                    id2 = null;
                    next = null;
                    return;
                }
                id1 = ki.next();
                vi = map.get(id1).entrySet().iterator();
            }
            final Map.Entry<Integer, T> e = vi.next();
            id2 = e.getKey();
            next = e.getValue();
        }

        @Override
        public boolean hasNext() {
            return id2 != null;
        }

        @Override
        public Map.Entry<C, T> next() {
            final C couple = newCouple(id1, id2);
            final T cur = next;
            peek();
            return new AbstractMap.SimpleImmutableEntry<C, T>(couple, cur);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public final static class Arcs<T> extends AdjacencyMap<Arc, T> {
        @Override
        protected Arc newCouple(Integer id1, Integer id2) {
            return new Arc(id1, id2);
        }
    }

    public final static class Edges<T> extends AdjacencyMap<Edge, T> {
        @Override
        protected Edge newCouple(Integer id1, Integer id2) {
            return new Edge(id1, id2);
        }

        @Override
        T put(Integer id1, Integer id2, T obj) {
            final T prev = super.put(id1, id2, obj);
            super.put(id2, id1, obj);
            return prev;
        }

        @Override
        T remove(Integer id1, Integer id2) {
            final T obj = super.remove(id1, id2);
            super.remove(id2, id1);
            return obj;
        }

        @Override
        public Set<Integer> vertices() {
            return Collections.unmodifiableSet(map.keySet());
        }

        @Override
        Iterator<T> valuesIterator() {
            return new ValuesIterator() {
                @Override
                void peek() {
                    do
                        super.peek();
                    while (next != null && id2.compareTo(id1) <= 0);
                }
            };
        }

        @Override
        Iterator<Edge> keysIterator() {
            return new KeysIterator() {
                @Override
                void peek() {
                    do
                        super.peek();
                    while (id2 != null && id2.compareTo(id1) <= 0);
                }
            };
        }

        @Override
        Iterator<Map.Entry<Edge, T>> entriesIterator() {
            return new EntriesIterator() {
                @Override
                void peek() {
                    do
                        super.peek();
                    while (next != null && id2.compareTo(id1) <= 0);
                }
            };
        }
    }
}
