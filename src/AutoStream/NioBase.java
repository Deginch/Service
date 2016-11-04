package AutoStream;

import ErrorLog.ErrorLog;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by root on 16-10-27.
 */
public class NioBase {
    private Selector selector;
    private NioChannel nioChannel;
    private boolean isRun = false;
    private MainThread mainThread = new MainThread();

    public NioBase(NioChannel nioChannel) {
        this.nioChannel = nioChannel;
    }

    /**
     * 开始运行
     *
     * @throws IOException
     */
    public void start() throws IOException {
        isRun = true;
        NioBase.this.selector = Selector.open();
        nioChannel.open(selector);
        mainThread.start();
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
        nioChannel.close();
    }

    class MainThread extends Thread {
        public void run() {
            int num = 0;
            while (isRun) {
                try {
                    num = selector.select(10 * 1000);
                    nioChannel.awake(num);
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
                            nioChannel.acceptable(key);
                        } else if (key.isConnectable()) {
                            nioChannel.connectable(key);
                        } else if (key.isReadable()) {
                            nioChannel.readable(key);
                        } else if (key.isWritable()) {
                            nioChannel.writable(key);
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

}
