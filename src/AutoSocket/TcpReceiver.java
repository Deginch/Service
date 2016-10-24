package AutoSocket;

/**
 * Created by degin on 2016/6/29.
 * 接受数据接口，凡是使用AutoSocket类的都需要继承此接口
 */
public interface TcpReceiver {
     void GetData(byte[] data);
}
