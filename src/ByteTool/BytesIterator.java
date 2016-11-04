package ByteTool;

import java.util.Iterator;

/**
 * Created by root on 16-10-21.
 */
public class BytesIterator implements Iterator {
    private byte[] array;
    private int index = 0;

    public BytesIterator(byte[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public Object next() {
        return array[index++];
    }

    /**
     * 重置到起点
     */
    public void reset() {
        index = 0;
    }

    /**
     * 设置当前起点
     * @param index
     */
    public void setPosition(int index){
        this.index =index>=array.length?(array.length-1):index;
    }
}
