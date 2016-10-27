package AutoStream;

/**
 * Created by root on 16-10-27.
 */
public enum AutoTcpType {
    Client,Server;
    public AutoStream getAutoTcp(String ip,int port,StreamReceiver receiver){
        switch (this){
            case Client:
                return new AutoNio(new NioTcpClient(ip,port,receiver));
            case Server:
                return new AutoNio(new NioTcpServer(ip,port,receiver));
        }
        return null;
    }
}
