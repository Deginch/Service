package AutoSocket;

import Tool.ErrorLog;
import Tool.ThreadPool;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

abstract public class AutoTcp {

    protected volatile boolean isConnected = false;
    protected String ip;
    protected int port;
    protected volatile boolean isRun = false;
    protected TcpReceiver tcpReceiver;
    public final static int TcpServerProtocol = 0;
    public final static int TcpClientProtocol = 1;
    protected final static int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    protected final static int DEFAULT_MAX_COUNT = 0;
    protected int reconnectTime = 3 * 1000;
    protected int maxCount = DEFAULT_MAX_COUNT;
    protected int restartCount = 0;
    protected int restartTime = -1;
    protected ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);

    protected Selector selector;
    protected Thread mainThread;
    public AutoTcp(String strIp, int strPort, TcpReceiver tcpReceiver) {
        ip = strIp;
        port = strPort;
        this.tcpReceiver = tcpReceiver;
    }

    abstract public void start();

    protected synchronized void handleReceivedData(byte[] data) {
        tcpReceiver.GetData(data);
    }

    abstract public void addSendingData(byte[] data);

    public void SetSleepTime(int strSleepTime) {
        this.reconnectTime = strSleepTime;
    }

    public static AutoTcp GetSocketByProtocolType(int protocolType, String strIp, int strPort, TcpReceiver tcpReceiver) {
        AutoTcp autoSocket;
        switch (protocolType) {
            case TcpClientProtocol:
                autoSocket = new AutoClientChannel(strIp, strPort, tcpReceiver);
                break;
            case TcpServerProtocol:
                autoSocket = new AutoServerChannel(strIp, strPort, tcpReceiver);
                break;
            default:
                return null;
        }
        return autoSocket;
    }


    public AutoTcp setBuffer(int bufferSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
        return this;
    }

    public AutoTcp setReconnectTime(int reconnectTime) {
        this.reconnectTime = reconnectTime;
        return this;
    }

    public AutoTcp setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }


    /**
     * 自动重启分钟
     *
     * @param restartTime
     * @return
     */
    public AutoTcp setRestartTime(int restartTime) {
        this.restartTime = restartTime;
        return this;
    }

    public void stop(){
        isRun=false;
        try {
            Iterator iterator = selector.keys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                key.channel().close();
                key.cancel();
            }
            selector.close();
            selector=null;
        } catch (IOException e) {
            ErrorLog.writeLog("Channel ip=" + ip + " port=" + port, e);
        }
        try {
            mainThread.join();
        } catch (InterruptedException e) {
            ErrorLog.writeLog("Channel ip=" + ip + " port=" + port, e);
        }
    }

    /**
     * 判断是否要重启
     *
     * @param num
     */
    public void rebootCheck(int num) {
        if (num == 0) {
            if (restartTime > 0 && restartCount++ > restartTime * 6) {//无数据时间达到restartTime
                ThreadPool.exec(new Runnable() {
                    @Override
                    public void run() {
                        ErrorLog.writeLog("ServerChannel ip=" + ip + " port=" + port + " reboot");
                        AutoTcp.this.stop();
                        AutoTcp.this.start();
                    }
                });
                restartCount = 0;
            }
        }
    }
}

