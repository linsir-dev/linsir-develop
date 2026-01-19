package com.linsir.core.c8;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
    public static void main(String[] args) throws UnknownHostException {
    InetAddress ip = InetAddress.getByName("localhost");
    InetAddress ip2 = InetAddress.getByName("USER-20220410RM");
    InetAddress ip3 = InetAddress.getByName("www.baidu.com");
    InetAddress ip4 = InetAddress.getByName("192.168.1.5");

    System.out.println(ip);
    System.out.println(ip2);
    System.out.println(ip3);
    System.out.println(ip4);

}
}
