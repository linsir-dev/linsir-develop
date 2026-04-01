# 建造者模式 - 代码指南

> 本文档详细说明建造者模式的代码实现和使用方法

***

## 一、项目结构

```
builder/
├── Player.java           # 产品类
├── PlayerBuilder.java    # 建造者接口
└── FullModeBuilder.java  # 具体建造者
```

***

## 二、代码详解

### 2.1 Player - 产品类

```java
package com.linsir.designpattern.builder;

public class Player {
    // 实际上这些类型都是有实体类的，为了简单起见，全都使用了String类型
    private String menu;        // 菜单
    private String playList;    // 播放列表
    private String mainWindow;  // 主窗口
    private String controller;  // 控制条
    private String keepList;    // 收藏列表

    public Player() {
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getPlayList() {
        return playList;
    }

    public void setPlayList(String playList) {
        this.playList = playList;
    }

    public String getMainWindow() {
        return mainWindow;
    }

    public void setMainWindow(String mainWindow) {
        this.mainWindow = mainWindow;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getKeepList() {
        return keepList;
    }

    public void setKeepList(String keepList) {
        this.keepList = keepList;
    }

    @Override
    public String toString() {
        return "Player{" +
                "menu='" + menu + '\'' +
                ", playList='" + playList + '\'' +
                ", mainWindow='" + mainWindow + '\'' +
                ", controller='" + controller + '\'' +
                ", keepList='" + keepList + '\'' +
                '}';
    }
}
```

**说明**：
- 包含多个属性的复杂对象
- 提供getter和setter方法
- 重写toString()方法便于调试

---

### 2.2 PlayerBuilder - 建造者接口

```java
package com.linsir.designpattern.builder;

public interface PlayerBuilder {
    PlayerBuilder buildMenu(String menu);
    PlayerBuilder buildPlayList(String playList);
    PlayerBuilder buildMainWindow(String mainWindow);
    PlayerBuilder buildController(String controller);
    PlayerBuilder buildKeepList(String keepList);
    Player build();
}
```

**说明**：
- 定义建造者的接口
- 每个方法返回建造者本身，支持链式调用
- `build()` 方法返回最终产品

---

### 2.3 FullModeBuilder - 具体建造者

```java
package com.linsir.designpattern.builder;

public class FullModeBuilder implements PlayerBuilder {
    private Player player = new Player();

    @Override
    public PlayerBuilder buildMenu(String menu) {
        player.setMenu(menu);
        return this;
    }

    @Override
    public PlayerBuilder buildPlayList(String playList) {
        player.setPlayList(playList);
        return this;
    }

    @Override
    public PlayerBuilder buildMainWindow(String mainWindow) {
        player.setMainWindow(mainWindow);
        return this;
    }

    @Override
    public PlayerBuilder buildController(String controller) {
        player.setController(controller);
        return this;
    }

    @Override
    public PlayerBuilder buildKeepList(String keepList) {
        player.setKeepList(keepList);
        return this;
    }

    @Override
    public Player build() {
        return player;
    }
}
```

**说明**：
- 实现建造者接口
- 维护一个产品对象
- 逐步构建产品的各个部分
- 返回建造者本身支持链式调用

***

## 三、使用示例

### 3.1 基本使用

```java
public class BuilderTest {
    public static void main(String[] args) {
        // 创建建造者
        PlayerBuilder builder = new FullModeBuilder();
        
        // 链式调用构建对象
        Player player = builder
                .buildMenu("显示菜单")
                .buildPlayList("显示播放列表")
                .buildMainWindow("显示主窗口")
                .buildController("显示控制条")
                .buildKeepList("显示收藏列表")
                .build();
        
        System.out.println(player);
    }
}
```

**输出**：
```
Player{menu='显示菜单', playList='显示播放列表', mainWindow='显示主窗口', controller='显示控制条', keepList='显示收藏列表'}
```

### 3.2 分步构建

```java
public class StepBuilderTest {
    public static void main(String[] args) {
        PlayerBuilder builder = new FullModeBuilder();
        
        // 第一步：构建菜单
        builder.buildMenu("显示菜单");
        
        // 第二步：构建播放列表
        builder.buildPlayList("显示播放列表");
        
        // 第三步：构建主窗口
        builder.buildMainWindow("显示主窗口");
        
        // 第四步：构建控制条
        builder.buildController("显示控制条");
        
        // 第五步：构建收藏列表
        builder.buildKeepList("显示收藏列表");
        
        // 最终构建
        Player player = builder.build();
        
        System.out.println(player);
    }
}
```

***

## 四、扩展：静态内部类建造者

### 4.1 改进的Player类

```java
package com.linsir.designpattern.builder;

public class Player {
    private final String menu;
    private final String playList;
    private final String mainWindow;
    private final String controller;
    private final String keepList;

    private Player(Builder builder) {
        this.menu = builder.menu;
        this.playList = builder.playList;
        this.mainWindow = builder.mainWindow;
        this.controller = builder.controller;
        this.keepList = builder.keepList;
    }

    // 只有getter，没有setter（不可变对象）
    public String getMenu() {
        return menu;
    }

    public String getPlayList() {
        return playList;
    }

    public String getMainWindow() {
        return mainWindow;
    }

    public String getController() {
        return controller;
    }

    public String getKeepList() {
        return keepList;
    }

    // 静态内部类建造者
    public static class Builder {
        private String menu;
        private String playList;
        private String mainWindow;
        private String controller;
        private String keepList;

        public Builder menu(String menu) {
            this.menu = menu;
            return this;
        }

        public Builder playList(String playList) {
            this.playList = playList;
            return this;
        }

        public Builder mainWindow(String mainWindow) {
            this.mainWindow = mainWindow;
            return this;
        }

        public Builder controller(String controller) {
            this.controller = controller;
            return this;
        }

        public Builder keepList(String keepList) {
            this.keepList = keepList;
            return this;
        }

        public Player build() {
            return new Player(this);
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "menu='" + menu + '\'' +
                ", playList='" + playList + '\'' +
                ", mainWindow='" + mainWindow + '\'' +
                ", controller='" + controller + '\'' +
                ", keepList='" + keepList + '\'' +
                '}';
    }
}
```

### 4.2 使用改进的建造者

```java
public class ImprovedBuilderTest {
    public static void main(String[] args) {
        // 使用静态内部类建造者
        Player player = new Player.Builder()
                .menu("显示菜单")
                .playList("显示播放列表")
                .mainWindow("显示主窗口")
                .controller("显示控制条")
                .keepList("显示收藏列表")
                .build();
        
        System.out.println(player);
    }
}
```

**优点**：
- 产品类不可变，线程安全
- 建造者是产品的静态内部类，关联更紧密
- 代码更简洁

***

## 五、最佳实践

1. **优先使用静态内部类建造者**：代码更简洁，产品不可变
2. **建造者方法返回this**：支持链式调用
3. **对必需参数进行校验**：在build()方法中校验必需参数
4. **考虑使用Lombok的@Builder注解**：简化建造者模式的实现

***

## 六、相关文档

- [设计模式概述](./01-builder-overview.md)
