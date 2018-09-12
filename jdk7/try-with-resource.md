# 更优雅的关闭资源

https://www.cnblogs.com/itZhy/p/7636615.html

## 以前的关闭方式

```java
public static void main(string[] args){
    FileInputStream is = null;
    try{
        is = new FileInputStream(new File("test"));
        System.out.println(is.read());
    }catch(IOException e){
        throw new RuntimeException(e.getMessage().e);
    }finally{
        if(is != null){
            try{
                is.close();
            }catch(IOException e){
                throw new RuntimeException(e.getMessage(),e);
            }
        }
    }
}
```

## JDK7之后新增的try-with-resource语法

```java
public static void main(String[] args){
    try(FileInputStream is = new FileInputStream(new File("test")){
        System.out.println(is.read());
    }catch(IOException e){
        throw new RuntimeException(e.getMessage(),e);
    }
}
```

- 将外部资源的句柄对象的创建放在try关键字后面的括号中,当这个try-catch代码块执行完毕后,Java会确保外部资源的close方法被调用.

- try-with-resource并不是JVM虚拟机新增的功能,只是JDK实现了一个语法糖,反编译之后:

  ```java
  public static void main(String[] args){
      try{
          FileInputStream is = new FileInputStream(new File("test"));
          Throwable var2 = null;
          try{
              System.out.println(is.read());
          }catch(Throwable var12){
              var2 = var12;
              throw var12;
          }finally{
              if(is != null){
                  try{
                      is.close()
                  }catch(Throwable var11){
                      var2.addSuppressed(var11);
                  }
              }else{
                  is.close();
              }
          }
      }catch(IOException e){
          throw new RuntimeException(var14.getMessage(),var14);
      }
  }
  ```

- 有一处特殊处理

  ```java
  var2.addSuppressed(var11);
  ```

  这是try-with-resource语法涉及的另外一个知识点,叫做异常抑制.

  当对外部资源进行处理(例如读或写)时,如果遭遇了异常,且在随后的关闭外部资源过程中,又遭遇了异常,那么你catch到的将会是对外部资源进行处理遭遇的异常,关闭资源时的异常将会被抑制,但不是丢弃,通过异常的getSuppressed方法,可以提取出被抑制的异常

## 当一个外部资源的句柄对象实现了AutoCloseable接口,JDK7中便可以利用try-with-resource语法更优雅的关闭资源,消除板式代码

## try-with-resource时,如果对外部资源的处理和对外部资源的关闭均遭遇了异常,'关闭异常'将会被抑制,'处理异常'会抛出,但'关闭异常'并没有丢失,而是放在'处理异常'的被抑制的异常列表中.