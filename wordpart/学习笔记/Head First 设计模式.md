# 一、设计模式入门

## 1. 设计原则
   - 封装变化；

   - 针对接口（超类型）编程，而不是针对实现编程；

   - 多用组合，少用继承；

   - 为交互对象之间的松耦合设计而努力；

   - 对扩展开放，对修改关闭；

   - 依赖倒置原则：要依赖抽象，不要依赖具体类；

   - 最少知识原则：减少与其他类和对象的耦合；

   - 好莱坞原则：允许低层组件挂靠，由高层组件决定什么时候调用低层组件；

     

## 2.  策略模式（游戏装备模式）

   **巧用面向对象编程中的多态、抽象用法，将类的一些特性抽象成为行为、算法、策略。并将这些行为、算法、策略使用“Has A”的方式包含在类中**。为了保证行为、算法、策略的灵活性和可变性，将这些行为、算法、策略先使用接口去定义描述，这些行为、算法、策略的不同实现形成了“行为族”、“算法族”、“策略族”。这样它们之间能够互相替换。此模式让行为、算法、策略的变化独立于使用算法的客户。



## 3. 观察者模式（布告板模式）

​	观察者模式中存在两种关系：主题和订阅者。**在主题中，维护了许多个订阅者信息，当主题信息发生变化时，会将变化通知给订阅者。**订阅者可以通过调用主题的API，将自己添加到主题的订阅者列表中，或者将自己从主题的订阅者列表中移除。总体来说，观察者模式定义了对象之间的一对多依赖，当主题对象改变状态的时候，他所有的依赖者都会收到通知并自动更新。



```java
interface Subject {
    registerObserver();
    removeObserver();
    notifyObservers();
}

interface Observer {
    update()
}

class ConcreteSubject implements Subject {
    registerObserver(){...}
    removeObserver(){...}
    notifyObservers(){...}
    
    getState()
    setState()
}

class ConcreteObserver implements Observer {
    update(){...}
}
```



## 4. 装饰者模式（奶茶模式）

​	装饰者模式中包含**组件类**和**装饰者类**，组件类和装饰者类实现了**相同的抽象组件接口**。装饰者类包含一个抽象组件接口对象，可以包含组件类对象或者装饰者类对象。通过组件类对象和装饰者类对象的**层层嵌套实现**，可以完成最终对象的装饰。

优势：层层装饰对象，减少创建新类

```java
interface Component {
    methodA();
    methodB();
}

class ConcreteComponent implements Component {
    methodA(){...}
    methodB(){...}
}

class Decorator implements Component {
    methodA(){...}
    methodB(){...}
}

class ConcreteDecoratorA extends Decorator {
    Component wrappedObj;
    
    methodA(){...}
    methodB(){...}
    newBehavior(){...}
}
```



## 5. 工厂模式

​	简单工厂不是一种设计模式，是一个编程习惯。将创建不同对象的代码逻辑封装起来，这样缩小需求变化造成的代码变化范围。

优势：对象创建解耦

```java
public class SimpleFactory {
    public Product createProduct(String type) {
        Product product = null;
        switch (type) {
            case "typeA":
                product = new ProductTypeA();
                break;
            case "typeB":
                product = new ProductTypeB();
                break;
            case "typeC":
                product = new ProductTypeC();
                break;
            default:
                product = new ProductDefault();
        }
        return product;
    }
}
```

​	**工厂方法模式**定义了一个创建对象的方法接口，但由子类决定要实例化的类是哪一个。工厂方法让类把实例化推迟到子类。

```java 
public abstract class Creator { 
    Product product = null;
	protected abstract Product factoryMethod();
    public Product Operation(){
        product = factoryMethod();
        product.operation();
        return product;
    }
}

public class ConcreteCreator extends Creator {
    @Override
    protected Product factoryMethod(){
        ...
    }
}
```

​	**抽象工厂模式**提供一个接口，用于创建相关或依赖对象的家族，而不需要明确指定具体类。即将原先的类对象使用方法创建，而不指定具体实现类对象。可以解耦类对象的创建。

```java
public interface AbstractFactory {
    //每个抽象方法都是一个工厂方法
    public PartA createPartA(); 
    public PartB createPartB();
}

public class ConcreteFactory implements AbstractFactory {
    @Override 
    public PartA createPartA(){
        return new ConcretePartA();
    }
    @Override
    public PartB createPartB(){
        return new ConcretePartB();
    }
}
```



## 6. 命令模式

​	**命令模式**将“请求”封装成对象，以便使用**不同的请求、队列或者日志**来参数化其他对象。命令模式也支持可撤销的操作。使用接口相同的Command对象，暴露出相同的execute接口。在不同的动作对象中，execute具体动作不同。

优势：统一化请求

```java
public interface Command {
    public void execute();
}

public class ConcreteCommand implements Command {
    Receiver receiver;
    public ConcreteCommand(){}
    public ConcreteCommand(Receiver receiver){
        this.receiver = receiver;
    }
    
    @Override
    public void execute(){
        receiver.doSomething();
    }
}

public class SimpleRemoteControl {
    Command slot;
    
    public SimpleRemoteControl(){}
    
    public SimpleRemoteControl(Command command){
        this.slot = command;
    }
    
    public void EventHappend(){
        solt.execute();
    }
}
```



## 7.适配器模式（兼容模式）

​	**适配器模式**将一个类的接口转换成客户期望的另一个接口。适配器让原本接口不兼容的类可以合作无间。

​	客户使用适配器的过程如下：

- 客户通过目标接口**调用适配器的方法**对适配器发起请求；
- 适配器使用被适配者接口把请求**转换成被适配者的一个或多个调用接口**；
- 客户接收到调用的结果，但并未察觉这一切是适配器在起转换作用。

```java
public interface Target {
   void method();
}

public class Adaptee {
    public void myMethod();
}

public class Adapter implements Target {
    Adaptee adaptee;
    public void method(){
        adaptee.myMethod();
    }
}

Target adapter = new Adapter(adaptee);
adapter.method();
```



## 8. 外观模式（子系统接口）

​	**外观模式**提供了一个统一的接口，用来访问子系统中的一群接口。外观定义了一个高层接口，让子系统更容易使用。主要用于将子系统的一系列操作封装成一个特定的接口，方便调用。一个子系统中可以生成多个不同的外观。



## 9.模板方法模式

​	**模板方法模式**是在一个方法中定义一个算法的骨架，而将一些步骤延迟到子类中。模板方法使得子类可以在不改变算法结构的情况下，重新定义算法中的某些步骤。

```java
abstract class AbstractClass {
    // 模板方法的精髓
    final void templateMethod() {
        primitiveOperation1();
        primitiveOperation2();
        concreteOperation();
        hook();
    }
    abstract void primitiveOperation1();
    abstract void primitiveOperation2();
    
    final void concreteOperation(){
        // 具体实现
    }
    void hook(){};
}
```



## 10 迭代器与组合模式

​	**迭代器模式**提供一种方法顺序访问一个聚合对象中的各个元素，而不暴露其内部的表示。

```java
public interface Iterator {
    boolean hasNext();
    Object next();
}

public class ConcreteIterator implements Iterator {
    public boolean hasNext(){
        //具体实现
    }
    public Object next(){
        //具体实现
    }
}
```

​	**组合模式**能够满足对象以树状层次嵌套的数据结构实现。

​	**组合模式**允许你将对象组合成树形结构来表现“整体/部分”层次结构。组合能让客户以一致的方式处理个别对象以及对象组合。

```java
public abstract class Component {
    public void operation(){
        throw new UnsupportedOperationException();
    }
    public void add(Component c){
        throw new UnsupportedOperationException();
    }
    public void remove(Component c){
        throw new UnsupportedOperationException();
    }
    public Component getChild(int index){
        throw new UnsupportedOperationException();
    }
}

public class Composite extends Component {
    public void operation(){
        // 具体实现
    }
    public void add(Component c){
        // 具体实现
    }
    public void remove(Component c){
        // 具体实现
    }
    public Component getChild(int index){
        // 具体实现
    }
}

public class Leaf extends Component {
    public void operation(){
        // 具体实现
    }
}
```

