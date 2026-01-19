package com.linsir.designpattern.flyweight;



import java.util.HashMap;
import java.util.Map;

public class ShapeFactory {
    private static final Map<Yanse, Shape> circleMap = new HashMap<Yanse, Shape>();

    public static Shape getCircle(Yanse color) {
        Shape circle = circleMap.get(color);

        if (circle == null) {
            circle = new Circle(color);
            circleMap.put(color, circle);
        }

        System.out.println(circleMap.size());

        return circle;
    }
}
