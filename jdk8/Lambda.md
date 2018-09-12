# 深入理解Java 8 Lambda

https://www.cnblogs.com/figure9/p/java-8-lambdas-insideout-language-features.html

## 我们把这些只拥有一个方法的接口称为*函数式接口* 

- API作者们可以通过`@FunctionalInterface`注解来显式指定一个接口是函数式接口 

## lambda表达式是匿名方法

```java
(int x , int y) -> x + y
() -> 42
(String s) -> { System.out.println(s); }
```

- lambda表达式的语法由参数列表,箭头符号和函数体组成.函数体既可以是一个表达式,也可以是一个语句块:
  - 表达式:表达式会被执行然后返回执行结果
  - 语句块:语句块中的语句会被依次执行,就像方法中的语句一样
    - return 会把控制权交给匿名方的调用者
    - break 和 continue 只能在循环中使用
    - 如果函数体有返回值,那么函数体内部的每一条路径都必须返回值

```java
FileFilter java = (File f) -> f.getName.endsWith('.java');

String user = doPrivileged() -> System.getProperty("user.name");

new Thread(() -> {
    connectToService();
    sendNotification();
}).start();
```

## 目标类型 

对于给定的lambda表达式,它的类型是由其上下文推导而来.

```java
ActionListener l = (ActionEvent e) -> ui.dazzle(e.getModifiers());
```

这里的类型是ActionListener.

这就意味着同样的Lambda表达式在不同上下文里可以拥有不同的类型 

```java
Callable<String> c = () -> "done";

PrivilegedAction<String> a = () -> "done";
```

lambda表达式只能出现在目标类型为函数式接口的上下文中 

- lambda表达式只能替代函数式接口(只有一个方法的接口)的实例

- lambda表达式的参数类型可以从目标类型中得出:

  ```java
  Comparator<String> c = (s1,s2) -> s1.compareToIgnoreCase(s2);
  ```

- 当lambda的参数只有一个而且他的类型可以被推导得知时,该参数列表外面的括号可以被省略

  ```java
  FileFileter java = f -> f.getName().endsWith(".java");
  button.addActionListener(e -> ui.dazzle(e.getModifiers()));
  ```

- lambda表达式并不是第一个拥有上下文相关类型的Java表达式: 泛型方法调用和'菱形'构造器调用也通过目标类型来进行类型推导:

  ```java
  List<String> ls = Collections.emptyList();
  List<Integer> li = Collections.emptyList();
  
  Map<String,Integer> m1 = new HashMap<>();
  Map<Integer,String> m2 = new HashMap<>();
  ```

## 目标类型的上下文 

- 变量声明
- 赋值
- 返回语句
- 数组初始化器
- 方法和构造方法的参数
- lambda表达式函数体
- 条件表达式 ( ? : )
- 转型cast表达式

变量声明,赋值,返回语句

```java
Comparator<String> c;
c = (String s1,String s2) -> s1.compareToIgnoreCase(s2);

public Runnable toDoLater(){
    return () -> {
        System.out.println("later");
    }
}
```

数组初始化器( 就是变量变成了数组元素 )

```java
new FileFilter[] { f -> f.exists(), f -> f.canRead(), f -> f.getName().startsWith("q") }
```

方法参数

```java
List<Person> ps = ...
Stream<String> names = ps.stream().map(p -> p.getName())	//语法见./Collection-stream.md
```

在上面的代码中,`ps`的类型是`List<Person>`,所以`ps.stream()`的返回类型是`Stream<Person>`.`map()`方法接收一个类型为`Function<T,R>`的函数式接口,这里T的类型即是`Stream`元素的类型,也就是`Person`,而`R`的类型未知.由于在*重载解析*之后lambda表达式的目标类型仍然未知,我们就需要推导`R`的类型:通过对lambda函数体进行类型检查,我们发现函数体返回`String`,因此,`R`类型是`String`,因而`map()`返回`Stream<String>`.

绝大多数情况下编译器都能解析出正确的类型,但如果碰到无法解析的情况,我们则需要:

- 使用显示lambda表达式,提供类型信息
- 把lambda表达式转型为`Function<Person,String>`
- 为泛型参数`R`提供一个实际类型. (`.<String>map(p -> p.getName())`)

lambda表达式为自己的函数体提供目标类型

```java
Supplier<Runnable> c = () -> () -> { System.out.println("hi"); };
```

条件表达式可以把目标类型分发给其子表达式

```java
Callable<Integer> c = flag ? (() -> 23) : (() -> 24);
```

转型表达式cast expression 可以显式提供lambda表达式的类型,这个特性在无法确认目标类型时非常有用:

```java
//Object o = () -> { System.out.println("hi"); }; //这段代码是非法的

Object o = (Runnable) () -> {System.out.println("hi");};
```

Java8 中泛型 目标类型 (下面代码在Java 7 中非法)

```java
List<String> ls = Collections.checkedList(new ArrayList<>(),String.class);

Set<Integer> si = flag ? Collection.singleton(23) : Collections.emptySet();
```

## 词法作用域 this

以下代码这里会打印两次`"Hello World"`

```java
public class Hello{
    Runnable r1 = () -> { System.out.println(this); }
    Runnabel r2 = () -> { System.out.println("Hello World"); }
    
    public String toString(){ return "Hello,World"; }
    
    public static void main(String[] args){
        new Hello().r1.run();
        new Hello().r2.run();       
    }
}
```

## 变量捕获 final

```java
Callable<String> helloCallable(String name){
    String hello = "Hello";
    return () -> (hello + ", " + name);
}
```

但外部的变量仍然不能被修改

```java
int sum = 0;
list.forEach(e -> { sum += e.size(); }); //这个句子是非法的
```

对值封闭,对变量开放

```java
int sum = 0;
list.forEach(e -> { sum+=e.size(); });	//Illegal,close over values

List<Integer> aList = new List<>();
list.forEach(e -> { aList.add(e); }); //Legal,open over variables
```

但我们有更好的方式来实现上面的效果

`java.util.stream`包提供了各种通用的和专用的规约操作.(sum,min,max)

```java
int sum = list.stream()
    		.mapToInt(e -> e.size())
    		.sum();
```

```java
int sum = list.stream()
    		.mapToInt(e -> e.size())
    		.reduce(0 , (x,y) -> x + y);
```

## 方法引用

```java
class Person{
    private final String name;
    private final int age;
    
    public int getAge(){return age;}
    public String getName(){return name;}
    
    ...
}

Person[] people = ...
Comparator<Person> byName = Comparator.comparing(p -> p.getName());
Arrays.sort(people,byName);
```

在这里我们可以用方法引用代替 lambda表达式:

```java
Comparator<Person> byName = Comparator.comparing(Person::getName);
```

这里的`Person::getName`可以被看做为lambda	表达式的简写.

尽管方法引用不一定会把语法变得更紧凑,但他拥有更明确的语义------如果我们想要调用的方法拥有一个名字,我们就可以通过他的名字直接调用它.

因为函数式接口的方法参数对应于隐式方法调用时的参数,所以被引用方法签名可以通过放宽类型,装箱以及组织到参数数组中的方式对其参数进行操作,就像在调用实际方法一样:

```java
Consumer<Integer> b1 = System::exit;
Consumer<String[]> b2 = Arrays::sort;
Consumer<String> b3 = MyProgram::main;
Runnable r = MyProgram::mapToInt;
```

## 方法引用的种类

- 静态方法引用  `ClassName::methodName`
- 实例上实例方法引用 `instanceReference::methodName`
- 超类上的实例方法引用 `super::methodName`
- 类型上的实例方法引用  `ClassName::methodName`
- 构造方法引用  `Class::new`
- 数组构造方法引用  `TypeName[]::new`







































