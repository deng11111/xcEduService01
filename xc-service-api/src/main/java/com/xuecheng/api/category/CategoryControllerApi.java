package com.xuecheng.api.category;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 课程分类api
 * @ClassName CategoryApi
 * @Author 邓元粮
 * @Date 2019/2/19 21:08
 * @Version 1.0
 **/
@Api(value = "课程分类管理",description = "课程分类管理",tags = {"课程分类管理"})
public interface CategoryControllerApi {

    @ApiOperation("课程分类列表")
    public CategoryNode list();

}
