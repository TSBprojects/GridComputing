package ru.sstu.vak.gridComputing.ui.gui;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public class ObservableLinkedList<E> implements ObservableList<E> {

    private List<E> nodes = new LinkedList<>();

    @Override
    public void addListener(ListChangeListener<? super E> listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(ListChangeListener<? super E> listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setAll(Collection<? extends E> col) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(E... elements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int from, int to) {
        for (int i = from; i < to; i++) {
            nodes.remove(i);
        }
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return nodes.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return nodes.iterator();
    }

    @Override
    public Object[] toArray() {
        return nodes.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return nodes.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return nodes.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return nodes.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return nodes.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return nodes.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return nodes.addAll(index,c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return nodes.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return nodes.retainAll(c);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public E get(int index) {
        return nodes.get(index);
    }

    @Override
    public E set(int index, E element) {
        return nodes.set(index,element);
    }

    @Override
    public void add(int index, E element) {
        nodes.add(index,element);
    }

    @Override
    public E remove(int index) {
        return nodes.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return nodes.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return nodes.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return nodes.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return nodes.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return nodes.subList(fromIndex,toIndex);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        throw new UnsupportedOperationException();
    }
}
