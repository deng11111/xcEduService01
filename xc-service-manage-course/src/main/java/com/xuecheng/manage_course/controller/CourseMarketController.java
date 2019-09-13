package com.xuecheng.manage_course.controller;

import com.xuecheng.api.CourseMarketControllerApi;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.CourseMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName CourseMarketController
 * @Description 课程营销
 * @Author 邓元粮
 * @Date 2019/5/12 17:06
 * @Version 1.0
 **/
@RestController
@RequestMapping("/courseMarket")
public class CourseMarketController implements CourseMarketControllerApi {

    @Autowired
    private CourseMarketService courseMarketService;

    @Override
    @GetMapping("/getCourseMarketById/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return courseMarketService.getCourseMarketById(courseId);
    }

    @Override
    @PostMapping("/updateCourseMarket/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        return courseMarketService.updateCourseMarket(id,courseMarket);
    }
}
