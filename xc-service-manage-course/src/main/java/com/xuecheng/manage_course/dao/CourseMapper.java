package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMapper {

   /**
    * 根据课程id查找课程
    * @Author 邓元粮
    * @MethodName findCourseBaseById
    * @Date 16:00 2019/2/17
    * @Param [id]
    * @return com.xuecheng.framework.domain.course.CourseBase
    **/
   CourseBase findCourseBaseById(String id);

   /**
    * 分页查询课程信息
    * @Author 邓元粮
    * @MethodName findPage
    * @Date 16:04 2019/2/17
    * @Param [courseListRequest]
    * @return org.springframework.data.domain.Page<com.xuecheng.framework.domain.course.ext.CourseInfo>
    **/
   Page<CourseInfo> findCourseListPage(CourseListRequest courseListRequest);
}
