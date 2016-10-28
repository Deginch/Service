package AutoStream;

/**
 * Created by root on 16-10-28.
 */
public abstract class StreamReceiver implements StreamReceiverWithObject {
    @Override
    public Object read(byte[] data, Object object) {
        return read(data);
    }

    public abstract Object read(byte[] data);
}
