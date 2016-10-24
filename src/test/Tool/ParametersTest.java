package test.Tool; 

import Tool.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After; 

/** 
* Parameters Tester. 
* 
* @author <Authors name> 
* @since <pre>十月 17, 2016</pre> 
* @version 1.0 
*/ 
public class ParametersTest { 
Parameters parameters=new Parameters(new String[]{"-start","start","-end","end"});


    @Before
public void before() throws Exception {}

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: getParameter(String key, String defaultParameter) 
* 
*/ 
@Test
public void testGetParameter() throws Exception {
    Assert.assertEquals(parameters.getParameter("start",""),"start");
    Assert.assertEquals(parameters.getParameter("end",""),"end");
    Assert.assertEquals(parameters.getParameter("test","test"),"test");
} 

/** 
* 
* Method: hasParameter(String key) 
* 
*/ 
@Test
public void testHasParameter() throws Exception {
    Assert.assertTrue(parameters.hasParameter("start"));
    Assert.assertTrue(parameters.hasParameter("end"));
    Assert.assertFalse(parameters.hasParameter("test"));
} 


} 
