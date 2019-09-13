package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName CategoryMapper
 * @Description TODO
 * @Author Administrator
 * @Date 2019/2/1921:18
 * @Version 1.0
 **/
@Mapper
public interface CategoryMapper {

    /**
     * 获取所有课程分类
     * @Author 邓元粮
     * @MethodName list
     * @Date 21:20 2019/2/19
     * @Param []
     * @return com.xuecheng.framework.domain.course.ext.CategoryNode
     **/
    public CategoryNode list();

}
