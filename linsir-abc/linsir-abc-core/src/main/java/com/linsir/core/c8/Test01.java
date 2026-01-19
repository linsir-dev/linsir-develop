package com.linsir.core.c8;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Test01 {
    public static void main(String[] args) {
        InetSocketAddress ip = new InetSocketAddress("192.168.1.5",8080);
        System.out.println(ip);
        System.out.println(ip.getHostName());
        System.out.println(ip.getPort());

        InetAddress ip2 = ip.getAddress();
        System.out.println(ip2);
        System.out.println(ip2.getHostName());
        System.out.println(ip2.getHostAddress());
        System.out.println(ip2.getAddress());
    }
}
