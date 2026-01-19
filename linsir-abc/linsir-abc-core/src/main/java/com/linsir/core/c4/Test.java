package com.linsir.core.c4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add(13);
        list.add(17);
        list.add(6);
        list.add(-1);
        list.add(2);
        list.add("abc");
        System.out.println(list);
        list.add(3,66);
        System.out.println(list);
        list.set(3,77);
        System.out.println(list);
        list.remove(2);
        System.out.println(list);
        list.remove("abc");
        System.out.println(list);
        Object o = list.get(0);
        System.out.println(o);

        //普通for循环：
        System.out.println("---------------------");
        for(int i = 0;i<list.size();i++){
            System.out.println(list.get(i));
        }
        //增强for循环：
        System.out.println("---------------------");
        for(Object obj:list){
            System.out.println(obj);
        }
        //迭代器：
        System.out.println("---------------------");
        Iterator it = list.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
    }
}