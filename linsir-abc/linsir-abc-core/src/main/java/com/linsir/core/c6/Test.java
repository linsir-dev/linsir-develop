package com.linsir.core.c6;

import java.io.*;

public class Test {
    public static void main(String[] args) throws IOException {
        File f1 = new File("D:\\123\\lol.jpg");//源文件
        File f2 = new File("D:\\123\\demo.jpg");//目标文件
        FileInputStream fs = new FileInputStream(f1);//有一个输入的管道
        FileOutputStream fo = new FileOutputStream(f2);//有一个输出管道
        BufferedInputStream bis = new BufferedInputStream(fs);//再套一个管道,增强
        BufferedOutputStream bos = new BufferedOutputStream(fo);//再套一个管道,增强

        //开始复制
        byte[] b = new byte [1024*6];
        int len = bis.read(b);
        while(len != -1){
            bos.write(b,0,len);
            len = bis.read(b);
        }
        bos.close();
        bis.close();
        fo.close();
        fs.close();

    }
}
