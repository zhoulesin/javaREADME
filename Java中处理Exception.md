# Java中的异常处理

## 在Finally块中清理资源或者使用try-with-resource语句

```java
public void automaticallyCloseResource(){
    File file = new File("./tmp.txt");
    
    try(FileInputStream is = new FileInputStream(file);){
        is.read();
    }catch(FileNotFoundException e){
        log.error(e);
    }catch(IOException e){
        log.error(e);
    }
}
```

## 指定具体的异常

```java
//这种使用毫无意义
public void doNotDoThis() throws Exception{}

//指定具体的异常
public void doThis() throws NumberFormatException{}
```

## 对异常进行文档说明

```java
/**
 *	This method does something extremely useful...
 *
 *	@param input
 *	@throws MyBussinessException if ... happens
 */
public void doSomething(String input) throws MyBussinessException{}
```

## 抛出异常的时候包含描述信息

```java
try{
    new Long("xyz");
}catch(NumberFormatException e){
    log.error(e);
}
```

## 首先捕获最具体的异常

```java
public void catchMostSpecificExceptionFirst(){
    try{
        doSomething("A message");
    }catch(NumberFormatException e){
        log.error(e);
    }catch(IllegalArgumentException e){
        log.error(e);
    }
}
```

## 不要捕获Throwable

```java
public void doNotCatchThrowable(){
    try{
        
    }catch(Throwable t){
        //never do this
    }
}
```

## 不要忽略异常(一定要打印异常)

```java
public void doNotIgnoreExceptions(){
    try{
        
    }catch(NumberFormatException e){
        log.error("This should never happen :" + e);
    }
}
```

## 不要记录并抛出异常

```java
try{
    new Long("xyz");
}catch(NumberFormatException e){
    log.error(e);
    //throw e;	//do not do this
}
```

## 包装自定义异常(包装异常时不要抛弃原始的异常)

```java
public void wrapException(String input) throws MyBusinessException {
    try{
        
    }catch(NumberFormatException e){
        throw new MyBusinessException("A message that describes the error.",e);
    }
}
```

