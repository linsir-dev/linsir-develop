package com.linsir.abc.dto;

public class Student {
    private String stuNum;
    private String stuName;
    private String stuGender;
    private int stuAge;
    private String stuPwd;

    public Student() {
    }

    public Student(String stuNum, String stuName, String stuGender, int stuAge, String stuPwd) {
        this.stuNum = stuNum;
        this.stuName = stuName;
        this.stuGender = stuGender;
        this.stuAge = stuAge;
        this.stuPwd = stuPwd;
    }

    @Override
    public String toString() {
        return "Student{" +
                "stuNum='" + stuNum + '\'' +
                ", stuName='" + stuName + '\'' +
                ", stuGender='" + stuGender + '\'' +
                ", stuAge=" + stuAge +
                ", stuPwd='" + stuPwd + '\'' +
                '}';
    }

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuGender() {
        return stuGender;
    }

    public void setStuGender(String stuGender) {
        this.stuGender = stuGender;
    }

    public int getStuAge() {
        return stuAge;
    }

    public void setStuAge(int stuAge) {
        this.stuAge = stuAge;
    }

    public String getStuPwd() {
        return stuPwd;
    }

    public void setStuPwd(String stuPwd) {
        this.stuPwd = stuPwd;
    }
}
