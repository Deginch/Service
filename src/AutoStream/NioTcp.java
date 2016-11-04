package AutoStream;

import ErrorLog.ErrorLog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by root on 16-10-27.
 */
public abstract class NioTcp implements NioChannel {
    protected String ip;
    protected int port;
    protected StreamReceiverWithObject receiver;
    protected final static int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    protected ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
    protected int reconnectTime =3*1000;
    protected Selector selector;
    protected int restartTime=-1;
    protected int restartCount=0;
    protected int maxCount=-1;
    public NioTcp(String ip, int port, StreamReceiverWithObject receiver) {
        this.ip=ip;
        this.port=port;
        this.receiver=receiver;
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
                handleData(data,key);
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

    protected void handleData(byte[] data,SelectionKey key){
        receiver.read(data,key.channel());
    }


    @Override
    public void writable(SelectionKey key) throws IOException {

    }


    protected void closeKey(SelectionKey key){
        try {
            key.channel().close();
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        }
        key.cancel();
    }

    @Override
    public void close() {

    }

    public static NioTcp getNioTcp(int NioType, String ip, int port, StreamReceiverWithObject receiver){
        switch (NioType){
            case AutoStream.TcpClient:
                return new NioTcpClient(ip,port,receiver);
            case AutoStream.TcpServer:
                return new NioTcpServer(ip,port,receiver);
            default:
                return new NioTcpClient(ip,port,receiver);
        }
    }

    public NioTcp setRestartTime(int restartTime) {
        this.restartTime = restartTime;
        return this;
    }

    public NioTcp setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public NioStream getNioStream(){
        return new NioStream(this);
    }
}
