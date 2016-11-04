package AutoStream;

/**
 * Created by root on 16-10-28.
 */
public abstract class Receiver implements StreamReceiverWithObject {
    @Override
    public Object read(byte[] data, Object object) {
        read(data);
        return null;
    }

    public abstract void read(byte[] data);
}
