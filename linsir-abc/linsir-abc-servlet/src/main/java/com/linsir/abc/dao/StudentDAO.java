package com.linsir.abc.dao;

import com.linsir.abc.dto.Student;
import com.linsir.abc.utils.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class StudentDAO {
    public Student queryStudentByNumAndPwd(String stuNum,String stuPwd) throws ClassNotFoundException, SQLException {
        Student student = null;
        try {
            String sql = "select stu_num stuNum,stu_name stuName,stu_gender stuGender,stu_age stuAge,stu_pwd stuPwd from students where stu_num=? and stu_pwd=?";
            QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());
            student = queryRunner.query(sql,new BeanHandler<Student>(Student.class),stuNum,stuPwd);

        }catch (Exception e){
            e.printStackTrace();
        }


        return student;
    }
}
