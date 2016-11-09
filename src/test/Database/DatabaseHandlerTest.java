package test.Database; 

import Database.DatabaseHandler;
import Database.Where;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import test.Mytest;
import java.util.*;

/** 
* DatabaseHandler Tester. 
* 
* @author <Authors name> 
* @since <pre>十一月 4, 2016</pre> 
* @version 1.0 
*/ 
public class DatabaseHandlerTest { 
DatabaseHandler handler;
@Before
public void before() throws Exception {
    handler=new DatabaseHandler();
} 

@After
public void after() throws Exception {
    handler.close();
} 

/** 
* 
* Method: reOpen() 
* 
*/ 
@Test
public void testReOpen() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: update(Object bean) 
* 
*/ 
@Test
public void testUpdate() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: delete(Objects bean) 
* 
*/ 
@Test
public void testDelete() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: insert(Object bean, boolean insertIndex) 
* 
*/ 
@Test
public void testInsertForBeanInsertIndex() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: insertDuplicateUpdate(Object bean) 
* 
*/ 
@Test
public void testInsertDuplicateUpdate() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: insert(List<?> list, boolean insertIndex) 
* 
*/ 
@Test
public void testInsertForListInsertIndex() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: refresh(Object bean) 
* 
*/ 
@Test
public void testRefresh() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: queryList(Factory factory, Where where) 
* 
*/ 
@Test
public void testQueryListForFactoryWhere() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: queryList(Class clazz, Where where) 
* 
*/ 
@Test
public void testQueryListForClazzWhere() throws Exception {
    List<Mytest> list=handler.queryList(Mytest.class, Where.noWhere());
    Assert.assertTrue(list.size()==10);
} 

/** 
* 
* Method: queryHashTable(Class clazz, Where where) 
* 
*/ 
@Test
public void testQueryHashTableForClazzWhere() throws Exception {
    Hashtable list=handler.queryHashTable(Mytest.class, Where.noWhere());
    Assert.assertTrue(list.size()==10);
} 

/** 
* 
* Method: queryHashTable(Factory factory, Where where) 
* 
*/ 
@Test
public void testQueryHashTableForFactoryWhere() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: queryOneData(String sql, Object[] params) 
* 
*/ 
@Test
public void testQueryOneData() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: execute(String sql, Object[] params) 
* 
*/ 
@Test
public void testExecute() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: close() 
* 
*/ 
@Test
public void testClose() throws Exception { 
//TODO: Test goes here... 
} 


/** 
* 
* Method: fillObject(ResultSet row, Set<Field> fields, Object bean) 
* 
*/ 
@Test
public void testFillObject() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = DatabaseHandler.getClass().getMethod("fillObject", ResultSet.class, Set<Field>.class, Object.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: getTableName(Class clazz) 
* 
*/ 
@Test
public void testGetTableName() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = DatabaseHandler.getClass().getMethod("getTableName", Class.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: getAllSelectedOrIndexField(Class clazz)
* 
*/ 
@Test
public void testGetAllSelectedField() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = DatabaseHandler.getClass().getMethod("getAllSelectedOrIndexField", Class.class);
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: getAllUpdateOrIndexField(Class clazz)
* 
*/ 
@Test
public void testGetAllUpdateField() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = DatabaseHandler.getClass().getMethod("getAllUpdateOrIndexField", Class.class);
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: getAllInsertOrIndexField(Class clazz)
* 
*/ 
@Test
public void testGetAllInsertField() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = DatabaseHandler.getClass().getMethod("getAllInsertOrIndexField", Class.class);
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: getIndexField(Class clazz) 
* 
*/ 
@Test
public void testGetIndexField() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = DatabaseHandler.getClass().getMethod("getIndexField", Class.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
