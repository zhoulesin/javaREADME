#  JAVA中的注解

https://www.toutiao.com/a6579389542464225799/

## 注解的本质

「java.lang.annotation.Annotation」

```txt
The common interface extended by all annotation types
所有的注解类型都继承自这个普通接口Annotation
```

JDK中内置的override注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override{
    
}
```

其实他的本质就是

```java
public interface Override extends Annotation{
}
```

解析一个类或者方法的注解往往有两种形式

- 编译器直接的扫描,指的是编译器在对java代码编译字节码的过程中会检测到某个类或者方法被一些注解修饰,这时他就会对于这些注解进行某些处理
- 运行期反射

@Override:一旦编译器检测到某个方法被修饰了@Override注解,编译器就会检查当前方法的方法签名是否真正重写了父类的某个方法,也就是比较父类中是否具有一个同样的方法签名.

## 元注解

元注解是用于修饰注解的注解,通常用在注解的定义上,

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override{
}
```

这是我们@Override注解的定义,你可以看到其中的@Target,@Retention两个注解就是元注解.

元注解一般用于指定某个注解生命周期以及作用目标等信息.

java中的元注解

```java
@Target: 注解的作用目标
@Retention: 注解的生命周期
@Documented: 注解是否应当被包含在JavaDoc文档中
@Inherited: 是否允许子类继承该注解
```

### @Target

@Target用于指明被修饰的注解最终可以作用的目标是谁,也就是指明,你的注解到底是用来修饰方法的,修饰类的,还是修饰字段属性的.

@Target定义如下

```java
@Document
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Target{
    ElementType[] value();
}
```

我们可以通过以下方式来为value传值

```java
@Target(value={ElementType.FIELD})
```

被这个@Target注解修饰的注解将只能作用在成员字段上,不能用于修饰方法或者类.其中,ElementType是一个枚举类型,有以下一些值:

```jvaa
ElementType.TYPE: 允许被修饰的注解作用在类.接口和枚举上
ElementType.FIELD:允许作用在属性字段上
ElementType.METHOD:允许作用在方法上
ElementType.PARAMETER:允许作用在方法参数上
ElementType.CONSTRUCTOR:允许作用在构造器上
ElementType.LOCAL_VARIABLE:允许作用在本地局部变量上
ElementType.ANNOTATION_TYPE:允许作用在注解上
ElementType.PACKAGE: 允许作用在报上
```

### @Retention

@Retention用于指明当前注解的生命周期,

```java
@Document
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Retention{
    RetentionPolicy value();
}
```

对value传值

```java
@Retention(value=RetentionPolicy.RUNTIME)
```

这里的RetentionPolicy取值

```java
RetentionPolicy.SOURCE:当前注解编译器可见,不会写入class文件
RetentionPolicy.CLASS:类加载阶段丢弃,会写入class文件
RetentionPolicy.RUNTIME,永久保存,可以反射获取
```

@Retention注解指定了被修饰的注解生命周期,一种是只能在编译期可见,变异后会被丢弃

一种会被编译器编译进class文件中,无论是累或是方法,乃至字段,他们都是有属性表的,而java虚拟机也定义了几种注解属性表用于存储注解信息,但是这种可见性不能带到方法区,类加载时会予以丢弃,

最后一种则是永久存在的可见性

### @Document

@Document注解修饰的注解,当我们执行Javadoc文档打包是会被保存进doc文档,反之将在打包时丢弃

### @Inherited

@Inherited注解修饰的注解是具有可继承性的,也就是说什么的注解修饰了一个类,而该类的子类将自动继承父类的该注解

### 其他三种

```java
@Overrider
@Deprecated
@SuppressWarnings
```

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Override{}
```

```java
@Document
@Retention(RetentionPolicy.RUNTIME)
@Target(value={CONSTRUCTOR,FIELD,LOCAL_VARIABLE,METHOD,PACKAGE,PARAMETER,TYPE})
public @interface Deprecated{}
```

```java
@Target(value={TYPE,FIELD,METHOD,PARAMTER,CONSTRUCTOR,LOCAL_VARIABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface SuppressWarning{
    String[] value();
}
```

他有一个value属性需要你主动传值,这个value代表的就是需要被压制的警告

```java
public static void main(String[] args){
    Date date = new Date(2018,7,11);
}
```

这段代码启动编译器会包警告

```txt
Warning:java:java.util.Date中的Date(int,int,int) 已过时
```

而我们不希望程序启动时,编译器检查代码中过时的方法,就可以使用@SuppressWarnings注解并给value属性传入一个参数来压制编译器的检查

```java
@SuppressWarnings(value="deprecated")
public static void main(String[] args){
    Date date = new Date(2018,7,11);
}
```

这样编译器不在检查main方法下是否有过时的方法调用,也就压制了编译器对这种警告的检查

java中还有很多的警告类型,他们都会对应一个字符串,通过设置value属性的值可以压制对这一类警告的检查

自定义注解

```java
public @interface InnotationName{}
```

## 注解与反射

```java
@Target(value={ElementType.FIELD,ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Hello{
    String value();
}
```

这里我们制定Hello这个注解只能修饰字段和方法,并且该注解永久存活,以便我们反射获取

虚拟机规范定义了一系列和注解相关的属性表,也就是说,无论字段,方法,或是类本身,如果被注解修饰了,就可以被写进字节码文件,属性表有以下几种

```java
RuntimeVisibleAnnotations:运行时可见的注解
RuntimeInVisibleAnnotations: 运行时不可见的注解
RuntimeVisibleParameterAnnotation:运行时可见的方法参数注解
RuntimeInVisibleParameterAnnotation:运行时不可见的方法参数注解
AnnotationDefault:注解类元素的默认值
```

对于一个类或者接口来说,Class类中提供了以下一些方法用于反射注解

```jvaa
getAnnotation:返回指定的注解
isAnnotationPresent:判定当前元素是否被指定注解修饰
getAnnotations:返回所有的注解
getDeclaredAnnotation:返回本元素的指定注解
getDeclaredAnnotations:返回本元素的所有注解,不包含父类继承而来的
```

## 例子

- 设置一个虚拟机启动参数,用户捕获JDK动态代理类

  ```java
  -Dsun.misc.ProxyGenerator.saveGeneratedFiles = true
  ```

- main函数

  ```java
  public class Test{
      @Hello("hello")
      public static void main(String[] args) throws NoSuchMethodException{
          Class cls = Test.class;
          Method method = cls.getMethod("main",String[].class);
          Hello hello = method.getAnnotation(Hello.class);
      }
  }
  ```

  

