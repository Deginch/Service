package test.AutoStream; 

import AutoStream.AutoNio;
import AutoStream.NioTcpClient;
import AutoStream.StreamReceiver;
import ErrorLog.ErrorLog;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* NioTcpClient Tester. 
* 
* @author <Authors name> 
* @since <pre>Oct 27, 2016</pre> 
* @version 1.0 
*/ 
public class NioTcpClientTest { 
AutoNio nio;
@Before
public void before() throws Exception {
    ErrorLog.setDebug();
    nio=new AutoNio(new NioTcpClient("127.0.0.1", 11111, new StreamReceiver() {
        @Override
        public void read(byte[] data) {
            ErrorLog.writeLog(new String(data));
        }
    }));
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: connect(Selector selector) 
* 
*/ 
@Test
public void testConnect() throws Exception {
    nio.start();
    while (true){
        Thread.sleep(1000);
    }
//TODO: Test goes here... 
} 

/** 
* 
* Method: open(Selector selector) 
* 
*/ 
@Test
public void testOpen() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: acceptable(SelectionKey key, Selector selector) 
* 
*/ 
@Test
public void testAcceptable() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: connectable(SelectionKey key, Selector selector) 
* 
*/ 
@Test
public void testConnectable() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: send(byte[] data, Selector selector) 
* 
*/ 
@Test
public void testSend() throws Exception { 
//TODO: Test goes here... 
} 


} 
