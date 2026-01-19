package com.linsir.designpattern;



import com.linsir.designpattern.abstractFactory.ConcreateFactory1;
import com.linsir.designpattern.abstractFactory.ConcreateFactory2;
import com.linsir.designpattern.abstractFactory.ProductA;
import com.linsir.designpattern.abstractFactory.ProductB;
import com.linsir.designpattern.adapter.BigPort;
import com.linsir.designpattern.adapter.SmallPort;
import com.linsir.designpattern.adapter.SmallToBig;
import com.linsir.designpattern.bridge.*;
import com.linsir.designpattern.builder.FullModeBuilder;
import com.linsir.designpattern.builder.Player;
import com.linsir.designpattern.builder.PlayerBuilder;
import com.linsir.designpattern.chainOfResponsibility.*;
import com.linsir.designpattern.command.Light;
import com.linsir.designpattern.command.LightOffCommand;
import com.linsir.designpattern.command.LightOnCommand;
import com.linsir.designpattern.command.RemoteControl;
import com.linsir.designpattern.composite.Directory;
import com.linsir.designpattern.composite.File;
import com.linsir.designpattern.decorator.CornDecorator;
import com.linsir.designpattern.decorator.IBread;
import com.linsir.designpattern.decorator.NormalBread;
import com.linsir.designpattern.facade.HomeTheaterFacade;
import com.linsir.designpattern.factory.IWork;
import com.linsir.designpattern.factory.IWorkFactory;
import com.linsir.designpattern.factory.StudentWorkFactory;
import com.linsir.designpattern.factory.WorkManager;
import com.linsir.designpattern.flyweight.ShapeFactory;
import com.linsir.designpattern.flyweight.Yanse;
import com.linsir.designpattern.interpreter.AddExpression;
import com.linsir.designpattern.interpreter.Expression;
import com.linsir.designpattern.interpreter.NumberExpression;
import com.linsir.designpattern.interpreter.SubtractExpression;
import com.linsir.designpattern.iterator.ConcreteCollection;
import com.linsir.designpattern.iterator.Iterator;
import com.linsir.designpattern.mediator.ConcreteChatMediator;
import com.linsir.designpattern.mediator.User;
import com.linsir.designpattern.memento.Caretaker;
import com.linsir.designpattern.memento.Originator;
import com.linsir.designpattern.observer.ConcreteObserver;
import com.linsir.designpattern.observer.ConcreteSubject;
import com.linsir.designpattern.observer.Observer;
import com.linsir.designpattern.protype.Author;
import com.linsir.designpattern.protype.Book;
import com.linsir.designpattern.proxy.Image;
import com.linsir.designpattern.proxy.ProxyImage;
import com.linsir.designpattern.singleton.*;
import com.linsir.designpattern.state.Elevator;
import com.linsir.designpattern.strategy.Addition;
import com.linsir.designpattern.strategy.Calculator;
import com.linsir.designpattern.strategy.Multiplication;
import com.linsir.designpattern.strategy.Subtraction;
import com.linsir.designpattern.templateMethod.AbstractClass;
import com.linsir.designpattern.templateMethod.ConcreteClass;
import com.linsir.designpattern.visitor.AreaCalculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Application {

    public static void main(String[] args){
        System.out.print("\n==========静态工厂方法另外一种实现方法   ===========\n");
        IWorkFactory studentworkfactory = new StudentWorkFactory();
        IWork work =studentworkfactory.getWork();
        work.doWork();

        System.out.print("\n==========静态工厂方法另外一种实现方法===========\n");
        IWork twork= WorkManager.getWork("t");
        twork.doWork();


        System.out.print("\n================单例的几种模式==========================\n");
        SingletonDemo1 singletonDemo1=SingletonDemo1.getInstance();
        SingletonDemo2 singletonDemo2=SingletonDemo2.getInstance();
        SingletonDemo3 singletonDemo3=SingletonDemo3.getInstance();
        SingletonDemo4 singletonDemo4=SingletonDemo4.getInstance();
        SingletonDemo5 singletonDemo5=SingletonDemo5.getInsatance();


        System.out.print("\n================抽象工廠模式==========================\n");
        ConcreateFactory1 concreateFactory1 = new ConcreateFactory1();
        ConcreateFactory2 concreateFactory2 = new ConcreateFactory2();

        ProductA a1=concreateFactory1.factoryA();
        ProductA a2=concreateFactory2.factoryA();
        ProductB b1=concreateFactory1.factoryB();
        ProductB b2=concreateFactory2.factoryB();

        a1.method1();
        a2.method1();
        b1.method1();
        b2.method1();

        System.out.print("\n================建造者模型==========================\n");
        PlayerBuilder playerBuilder = new FullModeBuilder();
        Player player =playerBuilder.build();
        System.out.print(player);

        System.out.print("\n================原型模式（Protype）==========================\n");
        Book book1 = new Book();
        Author author = new Author();
        author.setName("corn");
        author.setAge(100);
        book1.setAuthor(author);
        book1.setTitle("好记性不如烂博客");
        book1.setPageNum(230);

        Book book2 = book1.clone();

        System.out.println(book1 == book2);  // false
        System.out.println(book1.getPageNum() == book2.getPageNum());   // true
        System.out.println(book1.getTitle() == book2.getTitle());        // true
        System.out.println(book1.getAuthor() == book2.getAuthor());        // true

        System.out.print("\n================适配器模式（Adapter）==========================\n");
        SmallPort smallPort=new SmallPort() {
            public void userSmallPort() {
                System.out.print("使用的电脑小口");
            }
        };
        BigPort bigPort=new SmallToBig(smallPort);
        bigPort.userBigPort();

        System.out.print("\n================装饰模式（Adapter）==========================\n");
        IBread bread = new NormalBread();
        bread =new CornDecorator(bread);
        bread.process();


        System.out.println("\n================桥接模式（Bridge）==========================\n");
        Color redColor = new Red();
        Color blueColor = new Blue();

        Shape redCircle = new Circle(redColor);
        Shape blueSquare = new Square(blueColor);

        redCircle.draw();
        blueSquare.draw();


        System.out.println("\n================组合模式（Composite）==========================\n");

        File file1 = new File("file1.txt");
        File file2 = new File("file2.txt");
        Directory subDirectory = new Directory("Subdirectory");
        subDirectory.addComponent(file1);
        subDirectory.addComponent(file2);

        Directory rootDirectory = new Directory("Root");
        rootDirectory.addComponent(subDirectory);

        rootDirectory.display();

        System.out.println("\n================组合模式（Facade）==========================\n");

        HomeTheaterFacade homeTheaterFacade = new HomeTheaterFacade();
        // 准备观影
        homeTheaterFacade.watchMovie();
        //结束观影
        homeTheaterFacade.releaseMovie();

        System.out.println("\n================享元模式（Facade）==========================\n");

        // 在这个示例中，我们定义了一个Shape接口和一个具体的Circle类来表示享元对象。
        // ShapeFactory类负责管理共享的对象池，并通过getCircle方法返回共享的或新创建的圆形对象。

        //Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};

        Yanse[] yanses = {Yanse.Red,Yanse.GREEN,Yanse.BLUE,Yanse.YELLOW};

        for (int i = 0; i < 20; i++) {
            Yanse randomColor = yanses[(int) (Math.random() * yanses.length)];
            com.linsir.designpattern.flyweight.Shape circle =ShapeFactory.getCircle(randomColor);
            circle.draw((int) (Math.random() * 100), (int) (Math.random() * 100));
        }

        System.out.println("\n================代理模式（Facade）==========================\n");
        Image image = new ProxyImage("sample.jpg");

        // 图像未加载，直到调用display()方法
        image.display();

        // 图像已加载，无需再次创建
        image.display();

        System.out.println("\n================解释器模式（Interpreter）==========================\n");

        // 在这个示例中，我们构建了一个简单的数学表达式解释器，用于解释并计算基本的加法和减法表达式。
        // 这展示了解释器模式如何工作，将表达式解释成实际的结果。
        // 在实际应用中，解释器模式可以用于更复杂的领域，如编程语言解释器或规则引擎。

        // 构建表达式：2 + (3 - 1)
        Expression expression = new AddExpression(
                new NumberExpression(2),
                new SubtractExpression(
                        new NumberExpression(3),
                        new NumberExpression(1)
                )
        );

        // 解释并计算表达式的值
        int result = expression.interpret();
        System.out.println("Result: " + result); // 输出: Result: 4

        System.out.println("\n================ 模板方法模式（Template Method）==========================\n");


        AbstractClass template = new ConcreteClass();
        template.templateMethod();


        System.out.println("\n================ 责任链模式（Chain of Responsibility）==========================\n");


        // 在这个示例中，报销请求会依次被经理、部门主管和财务部门处理。根据报销金额的不同，请求会被传递到适当的处理者。
        ReimbursementHandler manager = new ManagerHandler();
        ReimbursementHandler departmentHead = new DepartmentHeadHandler();
        ReimbursementHandler finance = new FinanceHandler();


        manager.setSuccessor(departmentHead);
        departmentHead.setSuccessor(finance);

        ReimbursementRequest request1 = new ReimbursementRequest(800, "购买办公用品");
        ReimbursementRequest request2 = new ReimbursementRequest(3000, "参加培训");
        ReimbursementRequest request3 = new ReimbursementRequest(10000, "举办团建活动");


        manager.handleRequest(request1);
        manager.handleRequest(request2);
        manager.handleRequest(request3);

        System.out.println("\n================ 命令模式（Command）==========================\n");
        // 在这个示例中，我们使用命令模式创建了两种具体的命令：打开电灯和关闭电灯。
        // 遥控器可以设置不同的命令，然后按下按钮触发相应的操作。
        // 这样，命令发送者（遥控器）和命令接收者（电灯）之间实现了解耦。
        Light livingRoomLight = new Light();

        LightOnCommand livingRoomLightOn = new LightOnCommand(livingRoomLight);
        LightOffCommand livingRoomLightOff = new LightOffCommand(livingRoomLight);

        RemoteControl remote = new RemoteControl();

        remote.setCommand(livingRoomLightOn);
        remote.pressButton(); // 打开电灯

        remote.setCommand(livingRoomLightOff);
        remote.pressButton(); // 关闭电灯

        System.out.println("\n================ 迭代器模式（Iterator）==========================\n");

        ConcreteCollection<String> collection = new ConcreteCollection<>();
        collection.addItem("Item 1");
        collection.addItem("Item 2");
        collection.addItem("Item 3");

        Iterator<String> iterator = collection.createIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println("\n================ 中介者模式（Mediator）==========================\n");

        // 在这个示例中，ConcreteChatMediator 实现了 ChatMediator 接口，并管理用户列表。
        // 每个用户对象在构造时都传递了中介者实例，以便用户可以使用中介者发送和接收消息。


        ConcreteChatMediator chatMediator = new ConcreteChatMediator();

        User user1 = new User("Alice", chatMediator);
        User user2 = new User("Bob", chatMediator);
        User user3 = new User("Charlie", chatMediator);

        chatMediator.addUser(user1);
        chatMediator.addUser(user2);
        chatMediator.addUser(user3);

        user1.sendMessage("大家好！");
        user2.sendMessage("你好，Alice！");

        System.out.println("\n================ 备忘录模式（Memento）==========================\n");

        Originator originator = new Originator();
        Caretaker caretaker = new Caretaker();

        // 设置初始状态
        originator.setState("State 1");
        System.out.println("Current State: " + originator.getState());

        // 创建备忘录并保存状态
        caretaker.setMemento(originator.createMemento());

        // 修改状态
        originator.setState("State 2");
        System.out.println("Updated State: " + originator.getState());

        // 恢复之前的状态
        originator.restoreMemento(caretaker.getMemento());
        System.out.println("Restored State: " + originator.getState());


        System.out.println("\n================ 观察者模式（Observer）==========================\n");

        // 在这个示例中，ConcreteSubject 充当主题（被观察者），ConcreteObserver 充当观察者。
        // 主题维护一个观察者列表，并在状态变化时通知所有观察者。
        // 当主题的状态发生变化时，所有观察者都会被通知并更新自己的状态。

        ConcreteSubject subject = new ConcreteSubject();

        Observer observer1 = new ConcreteObserver("观察者1");
        Observer observer2 = new ConcreteObserver("观察者2");

        subject.addObserver(observer1);
        subject.addObserver(observer2);

        subject.setState(10);
        subject.setState(20);

        subject.removeObserver(observer1);

        subject.setState(30);

        System.out.println("\n================状态模式（State）==========================\n");

        // 在这个示例中，我们创建了一个模拟电梯系统，其中有开门状态和关门状态两个具体状态类，以及电梯类作为上下文类。
        // 通过切换状态，电梯在不同状态下有不同的行为表现。这就是状态模式的基本思想。

        Elevator elevator = new Elevator();

        elevator.openDoors(); // 当前状态：开门
        elevator.move();      // 当前状态：开门，无法移动
        elevator.closeDoors(); // 当前状态：关门
        elevator.move();       // 当前状态：移动中
        elevator.stop();       // 当前状态：停止
        elevator.openDoors();  // 当前状态：开门


        System.out.println("\n================策略模式（Strategy）==========================\n");

        Calculator calculator = new Calculator();

        calculator.setOperation(new Addition());
        int result1 = calculator.performOperation(5, 3);
        System.out.println("Addition Result: " + result1);

        calculator.setOperation(new Subtraction());
        int result2 = calculator.performOperation(10, 4);
        System.out.println("Subtraction Result: " + result2);

        calculator.setOperation(new Multiplication());
        int result3 = calculator.performOperation(6, 2);
        System.out.println("Multiplication Result: " + result3);


        System.out.println("\n================访问者模式（Visitor）==========================\n");

        // 在这个示例中，访问者模式允许我们在不修改形状类的情况下，通过实现不同的访问者来执行不同的操作，例如计算面积。
        // 这样，我们可以轻松地添加新的访问者来执行其他操作，同时保持形状类的不变。

        com.linsir.designpattern.visitor.Circle circle = new com.linsir.designpattern.visitor.Circle(5);
        com.linsir.designpattern.visitor.Rectangle rectangle = new com.linsir.designpattern.visitor.Rectangle(4, 6);

        AreaCalculator areaCalculator = new AreaCalculator();
        circle.accept(areaCalculator);
        rectangle.accept(areaCalculator);

        System.out.println("Total area: " + areaCalculator.getArea());


        System.out.println("\n==========================================\n");

        NumberFormat nf = new DecimalFormat("#,###.####");
            Double d = 554545.4545454;
            String str = nf.format(d);
            System.out.println(str);

    }
}
