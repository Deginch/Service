package Tool;

import java.util.Iterator;

/**
 * Created by sheldon on 16-10-18.
 */
public class ArrayIterator<E> implements Iterator {
    private E[] array;
    private int i;
    public ArrayIterator(E[] array){
        this.array=array;
    }

    @Override
    public boolean hasNext() {
        return i<array.length;
    }

    @Override
    public E next() {
        return array[i++];
    }
}
