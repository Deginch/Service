package AutoSocket;

import ErrorLog.ErrorLog;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by degin on 2016/7/29.
 */
public class AutoServerChannel extends AutoTcp implements Runnable {
    private ServerSocketChannel serverChannel;

    public AutoServerChannel(String strIp, int strPort, TcpReceiver tcpReceiver) {
        super(strIp, strPort, tcpReceiver);
    }


    @Override
    public void start() {
        try {
            isRun = true;
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port));
            this.selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            mainThread = new Thread(this);
            mainThread.start();
        } catch (IOException e) {
            ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
        }
    }

    public void run() {
        int num;
        while (isRun) {
            try {
                num = selector.select(10 * 1000);
            } catch (ClosedChannelException e) {
                ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                break;
            } catch (IOException e) {
                ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                continue;
            }
            rebootCheck(num);
            if (!isRun) break;
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = null;
                try {
                    key = (SelectionKey) iterator.next();
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        ErrorLog.writeLog("channel's count is " + (selector.keys().size() - 1) + ",ip=" + ip + ",port=" + port + ",remote =" + channel.getRemoteAddress().toString());
                        checkCount(channel);
                    } else if (key.isReadable()) {
                        restartCount = 0;
                        read(key);
                    } else if (key.isWritable()) {
                    }
                } catch (IOException e) {
                    ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                    if (key != null) {
                        try {
                            key.cancel();
                            key.channel().close();
                        } catch (IOException ex) {
                            ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                        }
                    }
                } catch (Exception e) {
                    ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                } finally {
                    iterator.remove();
                }
            }
        }
        ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port + ",main thread exit");
    }

    /**
     * 读取数据
     *
     * @param key
     * @throws IOException
     */
    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        buffer.clear();
        int count = channel.read(buffer);
        if (count > 0) {
            byte[] data = new byte[buffer.position()];
            System.arraycopy(buffer.array(), 0, data, 0, data.length);
            handleReceivedData(data);
        } else if (count == -1) {
            System.out.println("closing one channel, channel's count is " + (selector.keys().size() - 1) + ",ip=" + ip + ",port=" + port);
            channel.close();
            key.cancel();
        }
    }

    private void checkCount(Channel channel) {
        if (maxCount > DEFAULT_MAX_COUNT && selector.keys().size() > maxCount + 1) {
            ErrorLog.writeLog("too many channels,closing other channel ,ip=" + ip + ",port=" + port);
            Iterator iterator = selector.keys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                if (!key.channel().equals(serverChannel) && !key.channel().equals(channel)) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void addSendingData(byte[] data) {
        restartCount = 0;
        Iterator iterator = selector.keys().iterator();
        while (iterator.hasNext()) {
            SelectionKey key = (SelectionKey) iterator.next();
            if (!key.channel().equals(serverChannel)) {
                try {
                    ((SocketChannel) key.channel()).write(ByteBuffer.wrap(data));
                } catch (IOException e) {
                    ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port, e);
                }
            }
        }
    }
}
