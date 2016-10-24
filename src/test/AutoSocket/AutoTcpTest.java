package test.AutoSocket; 

import AutoSocket.AutoTcp;
import AutoSocket.TcpReceiver;
import Tool.ErrorLog;
import Tool.ThreadPool;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* AutoTcp Tester. 
* 
* @author <Authors name> 
* @since <pre>Oct 19, 2016</pre> 
* @version 1.0 
*/ 
public class AutoTcpTest { 
AutoTcp tcp;
@Before
public void before() throws Exception {
    ThreadPool.init();
    ErrorLog.setDebug();
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: start() 
* 
*/ 
@Test
public void testStart() throws Exception {
    tcp=AutoTcp.GetSocketByProtocolType(AutoTcp.TcpClientProtocol,"127.0.0.1",20003,new TcpReceiver(){
        @Override
        public void GetData(byte[] data) {
            ErrorLog.writeLog(data.length);
        }
    }).setRestartTime(1);
    tcp.start();
    while (true){
        Thread.sleep(1000);
    }
//TODO: Test goes here... 
} 

/** 
* 
* Method: stop() 
* 
*/ 
@Test
public void testStop() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: handleReceivedData(byte[] data) 
* 
*/ 
@Test
public void testHandleReceivedData() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: addSendingData(byte[] data) 
* 
*/ 
@Test
public void testAddSendingData() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: SetSleepTime(int strSleepTime) 
* 
*/ 
@Test
public void testSetSleepTime() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: GetSocketByProtocolType(int protocolType, String strIp, int strPort, TcpReceiver tcpReceiver) 
* 
*/ 
@Test
public void testGetSocketByProtocolType() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setBuffer(int bufferSize) 
* 
*/ 
@Test
public void testSetBuffer() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setReconnectTime(int reconnectTime) 
* 
*/ 
@Test
public void testSetReconnectTime() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setMaxCount(int maxCount) 
* 
*/ 
@Test
public void testSetMaxCount() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: setRestartTime(int restartTime) 
* 
*/ 
@Test
public void testSetRestartTime() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: rebootCheck(int num) 
* 
*/ 
@Test
public void testRebootCheck() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: run() 
* 
*/ 
@Test
public void testRun() throws Exception { 
//TODO: Test goes here... 
} 


} 
