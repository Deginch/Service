package AutoSocket;

import Tool.ErrorLog;
import Tool.ThreadPool;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by root on 16-8-31.
 */
public class AutoClientChannel extends AutoTcp implements Runnable {

    private SocketChannel channel;

    public AutoClientChannel(String strIp, int strPort, TcpReceiver tcpReceiver) {
        super(strIp, strPort, tcpReceiver);
    }

    @Override
    public void start() {
        isRun = true;
        connect();
        mainThread = new Thread(this);
        mainThread.start();
    }

    private void connect() {
        ErrorLog.writeLog("start connect ip:" + ip + ",port:" + port);
        if (channel != null && (channel.isConnected())) {
            return;
        } else if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                ErrorLog.writeLog(e);
            }
        }
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            if (selector == null) {
                selector = Selector.open();
            }
            channel.connect(new InetSocketAddress(ip, port));
            channel.register(selector, SelectionKey.OP_CONNECT);
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        }
    }

    @Override
    public void addSendingData(byte[] data) {
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

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        int num;
        while (isRun) {
            try {
                num = selector.select(10 * 1000);
            } catch (ClosedChannelException e) {
                ErrorLog.writeLog("ClientChannel ip=" + ip + " port=" + port, e);
                break;
            } catch (IOException e) {
                ErrorLog.writeLog("ClientChannel ip=" + ip + " port=" + port, e);
                continue;
            }
            rebootCheck(num);
            if (!isRun) break;
            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = null;
                try {
                    key = (SelectionKey) iterator.next();
                    if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            try {
                                channel.finishConnect();
                            } catch (ConnectException e) {
                                try {
                                    ErrorLog.writeLog("channel connecting error ,ip=" + ip + ",port=" + port, e);
                                    Thread.sleep(reconnectTime);
                                    connect();
                                    break;
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
                    } else if (key.isReadable()) {
                        restartCount = 0;
                        read(key);
                    } else if (key.isWritable()) {
                    }
                } catch (IOException e) {
                    ErrorLog.writeLog("ClientChannel ip=" + ip + " port=" + port, e);
                    if (key != null) {
                        try {
                            key.cancel();
                            key.channel().close();
                        } catch (IOException ex) {
                            ErrorLog.writeLog("ClientChannel ip=" + ip + " port=" + port, e);
                        }
                    }
                } catch (Exception e) {
                    ErrorLog.writeLog("ClientChannel ip=" + ip + " port=" + port, e);
                } finally {
                    iterator.remove();
                }
            }
        }
        ErrorLog.writeLog("ClientChannel ip=" + ip + " port=" + port + ",main thread exit");
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
            this.channel = null;
            connect();
        }
    }

}
