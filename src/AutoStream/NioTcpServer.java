package AutoStream;

import ErrorLog.ErrorLog;
import jdk.nashorn.internal.ir.IfNode;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by root on 16-10-27.
 */
public class NioTcpServer extends NioTcp {
    ServerSocketChannel serverChannel;
    HashMap<Object, SelectionKey> hashMap = new HashMap<>();

    public NioTcpServer(String ip, int port, StreamReceiverWithObject receiver) {
        super(ip, port, receiver);
    }

    @Override
    public void open(Selector selector) throws IOException {
        super.open(selector);
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void awake(int num) {

    }

    @Override
    public void acceptable(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        ErrorLog.writeLog("channel's count is " + (selector.keys().size() - 1) + ",ip=" + ip + ",port=" + port + ",remote =" + channel.getRemoteAddress().toString());
        checkCount(channel);
    }

    @Override
    public void connectable(SelectionKey key) throws IOException {

    }


    @Override
    public void send(byte[] data) {
        Iterator iterator = selector.keys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            if (!key.channel().equals(serverChannel)) {
                try {
                    ((SocketChannel) key.channel()).write(ByteBuffer.wrap(data));
                } catch (IOException e) {
                    ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                    closeKey(key);
                }
            }
        }
    }

    @Override
    protected void handleData(byte[] data, SelectionKey key) {
        Object index=receiver.read(data,key);
        if(index!=null&&!key.equals(hashMap.get(index))){
            hashMap.put(index,key);
        }
    }

    @Override
    public void send(byte[] data, Object object) {
        if(object!=null&&hashMap.containsKey(object)){
            try {
                ((SocketChannel)hashMap.get(object).channel()).write(ByteBuffer.wrap(data));
            } catch (IOException e) {
                ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                closeKey(hashMap.get(object));
            }
        }
    }


    /**
     * 检查已连接数据最大值是否已经超过指定值
     *
     * @param channel
     */
    private void checkCount(Channel channel) {
        if (maxCount > 0 && selector.keys().size() > maxCount + 1) {
            ErrorLog.writeLog("too many channels,closing other channel ,ip=" + ip + ",port=" + port);
            Iterator iterator = selector.keys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                if (!key.channel().equals(serverChannel) && !key.channel().equals(channel)) {
                    closeKey(key);
                }
            }
        }
    }


    @Override
    protected void closeKey(SelectionKey key) {
        super.closeKey(key);
        Iterator<SelectionKey> iterator = hashMap.values().iterator();
        while (iterator.hasNext()) {
            SelectionKey temp = iterator.next();
            if (temp.equals(key)) {
                iterator.remove();
            }
        }

    }
}
