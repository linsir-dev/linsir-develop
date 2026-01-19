package com.linsir.core.c5;

import java.util.Comparator;
import java.util.TreeSet;

public class Test {
    public static void main(String[] args) {
        //创建一个TreeSet:
        TreeSet<Student> ts = new TreeSet<>(new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        ts.add(new Student(10,"elili"));
        ts.add(new Student(8,"blili"));
        ts.add(new Student(4,"alili"));
        ts.add(new Student(9,"elili"));
        ts.add(new Student(10,"flili"));
        ts.add(new Student(1,"dlili"));
        System.out.println(ts.size());
        System.out.println(ts);
    }
}
