package com.linsir.abc.dao;

import com.linsir.abc.dto.Grade;
import com.linsir.abc.utils.DruidUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

public class GradeDAO {
    public Grade queryGradeBySnumAndCid(String snum,String cid){
        Grade grade = null;
        try{

            String sql = "select s.stu_num snum,s.stu_name sname,c.course_id cid,c.course_name cname ,g.score score " +
                    "from students s INNER JOIN grades g INNER JOIN courses c  on s.stu_num = g.snum and g.cid = c.course_id " +
                    "where s.stu_num=? and c.course_id=?";
            QueryRunner queryRunner = new QueryRunner(DruidUtils.getDataSource());
            grade = queryRunner.query(sql,new BeanHandler<Grade>(Grade.class),snum,cid);
        }catch (Exception e){
            e.printStackTrace();
        }
        return grade;
    }
}
