package com.linsir.core.c4;

import java.util.HashMap;

public class Test01 {
    public static void main(String[] args) {
        HashMap<Integer,String> hm = new HashMap<>();
        System.out.println(hm.put(12,"丽丽"));
        System.out.println(hm.put(7,"菲菲"));
        System.out.println(hm.put(19,"露露"));
        System.out.println(hm.put(12,"明明"));
        System.out.println(hm.put(6,"莹莹"));
        System.out.println("集合的长度："+hm.size());
        System.out.println("集合中内容查看："+hm);
    }
}

