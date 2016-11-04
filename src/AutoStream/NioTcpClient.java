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

/**
 * Created by root on 16-10-27.
 */
class NioTcpClient extends NioTcp {
    SocketChannel channel;
    protected volatile boolean isConnecting=false;

    public NioTcpClient(String ip, int port, StreamReceiverWithObject receiver) {
        super(ip, port, receiver);
    }

    public void connect() {
        isConnecting=true;
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
    public void open(Selector selector) throws IOException {
        super.open(selector);
        connect();
    }

    @Override
    public void awake(int num) {
        if (num == 0) {
            if (restartTime > 0 && restartCount++ > restartTime * 6) {//无数据时间达到restartTime
                ThreadPool.exec(new Runnable() {
                    @Override
                    public void run() {
                        restartCount=0;
                        ErrorLog.writeLog("reboot,ip:"+ip+",port:"+port);
                        if(channel.isRegistered()){
                            SelectionKey key=channel.keyFor(selector);
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
                isConnecting=false;
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                System.out.println("channel connected " + (selector.keys().size() - 1) + ",ip=" + ip + ",port=" + port);
            } catch (ConnectException e) {
                ErrorLog.writeLog("channel connecting error ,ip=" + ip + ",port=" + port, e);
                try {
                    Thread.sleep(reconnectTime);
                    key.cancel();
                    connect();
                    return;
                } catch (InterruptedException e1) {
                    ErrorLog.writeLog(e);
                }
            }
        } else {
            ErrorLog.writeLog("error");
        }
    }

    @Override
    public boolean send(byte[] data) {
        if (!isConnecting) {
            ErrorLog.writeLog("sending data");
            try {
                System.out.println(channel.isConnected());
                channel.write(ByteBuffer.wrap(data));
                return true;
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
                return false;
            }
        }else {
            return false;
        }
    }

    @Override
    public boolean send(byte[] data, Object object) {
        return send(data);
    }


}
