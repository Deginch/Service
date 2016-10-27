package AutoStream;

import ErrorLog.ErrorLog;
import Tool.ThreadPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by root on 16-10-27.
 */
public abstract class NioTcp implements NioStream{
    protected String ip;
    protected int port;
    protected int restartTime = -1;
    protected int restartCount = 0;
    protected StreamReceiver receiver;
    protected final static int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    protected ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    protected int reconnectTime =3*1000;
    protected Selector selector;

    public NioTcp(String ip,int port,StreamReceiver receiver) {
        this.ip=ip;
        this.port=port;
        this.receiver=receiver;
    }

    @Override
    public void awake(int num) {

    }

    @Override
    public void open(Selector selector) throws IOException {
        this.selector=selector;
    }

    @Override
    public void readable(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        buffer.clear();
        try {
            int count = channel.read(buffer);
            if (count > 0) {
                byte[] data = new byte[buffer.position()];
                System.arraycopy(buffer.array(), 0, data, 0, data.length);
                receiver.read(data);
            } else if (count == -1) {
                System.out.println("closing one channel,ip=" + ip + ",port=" + port);
                closeKey(key);
                ((SocketChannel) key.channel()).socket().close();
            }
        }catch (IOException e){
            ErrorLog.writeLog(e);
            closeKey(key);
        }
    }

    @Override
    public void writable(SelectionKey key) throws IOException {

    }


    /**
     * 关闭指定SelectKey
     *
     * @param key
     */
    protected void closeKey(SelectionKey key) {
        try {
            if (key != null) {
                key.cancel();
                key.channel().close();
            }
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        }
    }

    @Override
    public void setRestartTime(int restartTime) {
        this.restartTime=restartTime;
    }
}
