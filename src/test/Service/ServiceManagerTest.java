package test.Service; 

import Database.JdbcPool;
import Service.ServiceManager;
import ErrorLog.ErrorLog;
import Tool.ThreadPool;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

/** 
* ServiceManager Tester. 
* 
* @author <Authors name> 
* @since <pre>Oct 20, 2016</pre> 
* @version 1.0 
*/ 
public class ServiceManagerTest {
ServiceManager manager=new ServiceManager(new testFactory());
@Before
public void before() throws Exception {
    ThreadPool.init();
    ErrorLog.setDebug();
    manager.start();
} 

@After
public void after() throws Exception {
    JdbcPool.close();
} 

/** 
* 
* Method: start() 
* 
*/ 
@Test
public void testStart() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: stop() 
* 
*/ 
@Test
public void testStop() throws Exception {
    Thread.sleep(60*1000);
    manager.stop();
//TODO: Test goes here... 
} 

/** 
* 
* Method: reboot() 
* 
*/ 
@Test
public void testReboot() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getIndexField() 
* 
*/ 
@Test
public void testGetIndexField() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getTableName() 
* 
*/ 
@Test
public void testGetTableName() throws Exception { 
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


/** 
* 
* Method: killCallBack() 
* 
*/ 
@Test
public void testKillCallBack() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = ServiceManager.getClass().getMethod("killCallBack"); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
