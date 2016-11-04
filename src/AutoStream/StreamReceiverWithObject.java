package AutoStream;

/**
 * Created by root on 16-10-27.
 */
public interface StreamReceiverWithObject {
    Object read(byte[] data,Object object);
}
