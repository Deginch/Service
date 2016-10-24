package ServiceHandler;

/**
 * Created by degin on 2016/7/3.
 * 凡是想要通过ServiceCommand来自动实现command都需要实现此类
 */
public interface Commander {
    void start();
    void stop();
    void reboot();
}
