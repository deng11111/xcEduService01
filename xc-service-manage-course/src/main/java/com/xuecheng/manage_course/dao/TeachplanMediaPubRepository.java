package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 课程计划-媒资文件绑定关系持久层
 * @ClassName TeachplanMediaRepository
 * @Description TODO
 * @Author Administrator
 * @Date 2019/9/78:44
 * @Version 1.0
 **/
public interface TeachplanMediaPubRepository extends JpaRepository<TeachplanMediaPub, String> {

    /*
     *@Description 根据课程id删除课程计划-媒资文件绑定关系
     * @Author 邓元粮
     * @MethodName deleteByCourseId
     * @Datetime 10:25 2019/9/8
     * @Param [courseId]
     * @return long
     **/
    long deleteByCourseId(String courseId);

}
