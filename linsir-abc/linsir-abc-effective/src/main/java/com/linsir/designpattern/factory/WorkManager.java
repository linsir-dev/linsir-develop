package com.linsir.designpattern.factory;

public class WorkManager {
    public static IWork getWork(String name)
    {
        if (name.equals("s"))
        {
            return new StudentWork();
        }
        else
        {
            return  new TeacherWork();
        }
    }
}
