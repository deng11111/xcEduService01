package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CourseMarketRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * @ClassName CourseMarketService
 * @Author 邓元粮
 * @Date 2019/5/12 17:10
 * @Version 1.0
 **/
@Service
public class CourseMarketService {

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    /**
     * 根据课程id获取课程营销信息
     * @Author 邓元粮
     * @MethodName getCourseMarketById
     * @Date 17:21 2019/5/12
     * @Param [courseId]
     * @return com.xuecheng.framework.domain.course.CourseMarket
     **/
    public CourseMarket getCourseMarketById(String courseId) {
        if (StringUtils.isBlank(courseId)){
            return null;
        }
        Optional<CourseMarket> optional = courseMarketRepository.findById(courseId);

        if (optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 新增或者更新课程营销信息
     * @Author 邓元粮
     * @MethodName updateCourseMarket
     * @Date 17:22 2019/5/12
     * @Param [id, courseMarket]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        if (courseMarket == null){
            return new ResponseResult(CommonCode.INVALID_PARAM);
        }
        courseMarketRepository.save(courseMarket);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
