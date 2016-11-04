package AutoStream;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Created by root on 16-10-27.
 */
public interface NioChannel {

    void open(Selector selector) throws IOException;

    void awake(int num);

    void acceptable(SelectionKey key)throws IOException;

    void readable(SelectionKey key)throws IOException;

    void writable(SelectionKey key)throws IOException;

    void connectable(SelectionKey key)throws IOException;

    boolean send(byte[] data);

    boolean send(byte[] data,Object object);

    void close();

}
