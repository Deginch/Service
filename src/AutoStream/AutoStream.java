package AutoStream;

import java.io.IOException;

/**
 * Created by root on 16-10-27.
 */
public interface AutoStream {
    public abstract void start() throws IOException;
    public abstract void send(byte[] data);
    public abstract void stop();

}
