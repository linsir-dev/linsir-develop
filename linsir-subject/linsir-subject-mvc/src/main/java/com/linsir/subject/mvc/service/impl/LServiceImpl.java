package com.linsir.subject.mvc.service.impl;


import com.linsir.subject.mvc.annotation.LinsirService;
import com.linsir.subject.mvc.service.LService;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/1 23:22
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
@LinsirService("lServiceImpl")
public class LServiceImpl implements LService {
    @Override
    public String query(String name, String age) {
        return "=========name:"+name+"+++++++++++++age:"+age;
    }
}
