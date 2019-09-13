package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName CoursePicRepository
 * @Description 课程图片持久层
 * @Author Administrator
 * @Date 2019/6/89:30
 * @Version 1.0
 **/
public interface CoursePicRepository extends JpaRepository<CoursePic,String> {
    /**
     * 根据课程id删除绑定关系
     * @Author 邓元粮
     * @MethodName deleteByCourseid
     * @Date 15:59 2019/6/8
     * @Param [courseId]
     * @return long
     **/
    long deleteByCourseid(String courseId);
}
