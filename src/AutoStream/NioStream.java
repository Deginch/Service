package AutoStream;

import java.io.IOException;

/**
 * Created by root on 16-10-28.
 */
public class NioStream extends AutoStream {
    private NioBase nioBase;
    private NioChannel nioChannel;
    public NioStream(int NioType, String ip, int port, StreamReceiverWithObject receiver) {
        nioChannel=NioTcp.getNioTcp(NioType,ip,port,receiver);
        nioBase=new NioBase(nioChannel);
    }

    public NioStream(NioChannel nioChannel){
        this.nioChannel=nioChannel;
        nioBase=new NioBase(nioChannel);

    }

    @Override
    public void start() throws IOException {
        nioBase.start();
    }

    @Override
    public void send(byte[] data) {
        nioChannel.send(data);
    }


    public boolean send(byte[] data, Object index) {
        return nioChannel.send(data,index);
    }

    @Override
    public void stop() {
        nioBase.stop();
    }
}
