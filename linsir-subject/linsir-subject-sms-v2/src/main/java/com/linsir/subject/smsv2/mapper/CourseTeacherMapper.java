package com.linsir.subject.smsv2.mapper;


import com.linsir.subject.smsv2.entity.Course;
import com.linsir.subject.smsv2.entity.CourseTeacher;
import com.linsir.subject.smsv2.entity.CourseTeacherInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Auther: auggie
 * @Date: 2022/2/10 16:43
 * @Description: CourseTeacherMapper
 * @Version 1.0.0
 */
@Repository
@Mapper
public interface CourseTeacherMapper {

    @Insert("INSERT INTO linsir_subject_sms_v2.ct (cid, tid, term) VALUES (#{cid}, #{tid}, #{term})")
    public boolean insertCourseTeacher(@Param("cid") Integer cid,
                                       @Param("tid") Integer tid,
                                       @Param("term") String term);

    public List<CourseTeacher> findBySearch(@Param("cid") Integer cid,
                                            @Param("tid") Integer tid,
                                            @Param("term") String term);

    public List<Course> findMyCourse(@Param("tid") Integer tid,
                                     @Param("term") String term);

    public List<CourseTeacherInfo> findCourseTeacherInfo(@Param("tid") Integer tid,
                                                         @Param("tname") String tname,
                                                         @Param("tFuzzy") Integer tFuzzy,
                                                         @Param("cid") Integer cid,
                                                         @Param("cname") String cname,
                                                         @Param("cFuzzy") Integer cFuzzy);

    @Delete("DELETE FROM linsir_subject_sms_v2.ct WHERE cid = #{c.cid} AND tid = #{c.tid}")
    public boolean deleteById(@Param("c") CourseTeacher courseTeacher);
}
