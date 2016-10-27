package AutoStream;

import ErrorLog.ErrorLog;
import Tool.ThreadPool;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Created by root on 16-10-27.
 */
public class AutoNio implements AutoStream{
    private Selector selector;

    private NioStream nioStream;
    private boolean isRun = false;
    private MainThread mainThread=new MainThread();

    private int restartTime =-1;
    private int restartCount=0;

    public AutoNio(NioStream nioStream) {
        this.nioStream = nioStream;
    }

    /**
     * 发送数据
     *
     * @param data
     */
    public void send(byte[] data) {
        restartCount=0;
        nioStream.send(data);
    }

    /**
     * 开始运行
     *
     * @throws IOException
     */
    public void start() throws IOException {
        isRun = true;
        this.selector = Selector.open();
        nioStream.open(selector);
        mainThread.start();
    }

    class MainThread extends Thread {
        public void run() {
            while (isRun) {
                try {
                    nioStream.awake(selector.select(10 * 1000));
                } catch (IOException e) {
                    ErrorLog.writeLog(e);
                } catch (ClosedSelectorException e) {
                    break;
                }
                if (!isRun) break;
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = null;
                    try {
                        key = iterator.next();
                        if (key.isAcceptable()) {
                            nioStream.acceptable(key);
                        } else if (key.isConnectable()) {
                            nioStream.connectable(key);
                        } else if (key.isReadable()) {
                            restartCount=0;
                            nioStream.readable(key);
                        } else if (key.isWritable()) {
                            nioStream.writable(key);
                        }
                    } catch (IOException e) {
                        ErrorLog.writeLog(e);
                    } catch (Exception e) {
                        ErrorLog.writeLog(e);
                    } finally {
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * 停止运行
     */
    public void stop() {
        isRun = false;
        try {
            Iterator iterator = selector.keys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = (SelectionKey) iterator.next();
                key.channel().close();
                key.cancel();

            }
            selector.close();
            selector = null;
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        }
        try {
            mainThread.join();
        } catch (InterruptedException e) {
            ErrorLog.writeLog(e);
        }

    }


    public void setRestartTime(int restartTime) {
        nioStream.setRestartTime(restartTime);
    }



}
