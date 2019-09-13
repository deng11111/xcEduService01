package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName TeachplanMapper
 * @Description TODO
 * @Author Administrator
 * @Date 2019/2/1217:48
 * @Version 1.0
 **/
@Mapper
public interface TeachplanMapper {
    /**
     * 根据课程id查询课程计划
     * @Author 邓元粮
     * @MethodName selectList
     * @Date 17:48 2019/2/12
     * @Param [courseId]
     * @return com.xuecheng.framework.domain.course.ext.TeachplanNode
     **/
    TeachplanNode selectList(String courseId);
}
