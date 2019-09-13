package com.xuecheng.api;

import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName CourseMarketControllerApi
 * @Description 课程营销
 * @Author Administrator
 * @Date 2019/5/1217:02
 * @Version 1.0
 **/
@Api(value = "课程营销",description = "课程营销",tags = {"课程营销"})
public interface CourseMarketControllerApi {

    @ApiOperation("获取课程营销信息")
    public CourseMarket getCourseMarketById(String courseId);

    @ApiOperation("更新课程营销信息")
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);
}
