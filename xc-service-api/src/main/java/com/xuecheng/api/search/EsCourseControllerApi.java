package com.xuecheng.api.search;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName EsCourseControllerApi
 * @Author 邓元粮
 * @Date 2019/8/2 7:52
 * @Version 1.0
 **/
@Api(value = "课程搜索",description = "课程搜索",tags = {"课程搜索"})
public interface EsCourseControllerApi {
    @ApiOperation("课程搜索")
    public QueryResponseResult<CoursePub> list(Integer page, Integer size,
                                               CourseSearchParam courseSearchParam) throws IOException;
    @ApiOperation("根据id查询课程信息")
    Map<String,CoursePub> getall(String id);

    @ApiOperation("根据课程计划查询媒资信息")
    TeachplanMediaPub getmedia(String teachplanId);
}
