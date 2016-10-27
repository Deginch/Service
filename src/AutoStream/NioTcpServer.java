package AutoStream;

import ErrorLog.ErrorLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by root on 16-10-27.
 */
public class NioTcpServer extends NioTcp {
    ServerSocketChannel serverChannel;
    private int maxCount=-1;

    public NioTcpServer(String ip, int port, StreamReceiver receiver) {
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


    /**
     * 检查已连接数据最大值是否已经超过指定值
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
}
