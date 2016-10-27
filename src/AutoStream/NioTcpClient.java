package AutoStream;

import ErrorLog.ErrorLog;
import Tool.ThreadPool;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by root on 16-10-27.
 */
public class NioTcpClient extends NioTcp {
    SocketChannel channel;

    public NioTcpClient(String ip, int port, StreamReceiver receiver) {
        super(ip, port, receiver);
    }

    public void connect() {
        try {
            ErrorLog.writeLog("start connect ip:" + ip + ",port:" + port);
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(ip, port));
            channel.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        }
    }

    @Override
    public void awake(int num) {
        super.awake(num);
        if (num == 0) {
            if (restartTime > 0 && restartCount++ > restartTime * 6) {//无数据时间达到restartTime
                ThreadPool.exec(new Runnable() {
                    @Override
                    public void run() {
                        Iterator iterator = selector.keys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = (SelectionKey) iterator.next();
                            closeKey(key);
                        }
                        connect();
                    }
                });
                restartCount = 0;
            }
        }
    }

    @Override
    public void open(Selector selector) throws IOException {
        super.open(selector);
        connect();
    }

    @Override
    public void readable(SelectionKey key) throws IOException {
        super.readable(key);
        if (!key.isValid()) {
            connect();
        }
    }

    @Override
    public void acceptable(SelectionKey key) throws IOException {

    }

    @Override
    public void connectable(SelectionKey key) throws IOException {
        if (channel.isConnectionPending()) {
            try {
                channel.finishConnect();
            } catch (ConnectException e) {
                try {
                    ErrorLog.writeLog("channel connecting error ,ip=" + ip + ",port=" + port, e);
                    Thread.sleep(reconnectTime);
                    connect();
                    return;
                } catch (InterruptedException e1) {
                    ErrorLog.writeLog(e);
                }
            }
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            System.out.println("channel connected " + (selector.keys().size() - 1) + ",ip=" + ip + ",port=" + port);
        } else {
            ErrorLog.writeLog("error");
        }
    }

    @Override
    public void send(byte[] data) {
        restartCount = 0;
        if (channel.isConnected()) {
            try {
                channel.write(ByteBuffer.wrap(data));
            } catch (IOException e) {
                ErrorLog.writeLog(e);
                try {
                    channel.close();
                    channel = null;
                } catch (IOException e1) {
                    ErrorLog.writeLog(e);
                }
                try {
                    Thread.sleep(reconnectTime);
                } catch (InterruptedException e1) {
                    ErrorLog.writeLog(e1);
                }
                connect();
            }
        } else {
            try {
                Thread.sleep(reconnectTime);
            } catch (InterruptedException e1) {
                ErrorLog.writeLog(e1);
            }
            connect();
        }
    }
}
