package AutoStream;

import java.io.IOException;

/**
 * Created by root on 16-10-27.
 */
public abstract class AutoStream {

    public abstract void start() throws IOException;

    public abstract void send(byte[] data);

    public abstract void stop();

    public final static int TcpClient = 1;
    public final static int TcpServer = 0;
}
