package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.dao.CoursePicRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @ClassName CoursePicService
 * @Author 邓元粮
 * @Date 2019/6/8 9:29
 * @Version 1.0
 **/
@Service
public class CoursePicService {

    @Autowired
    private CoursePicRepository coursePicRepository ;

    /**
     * 保存课程图片
     * @Author 邓元粮
     * @MethodName addCoursePic
     * @Date 10:11 2019/6/8
     * @Param [courseId, pic]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {
        if (StringUtils.isEmpty(courseId)|| StringUtils.isEmpty(pic)){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (!optional.isPresent()){
            coursePic = new CoursePic();
        }else{
            coursePic = optional.get();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     *  获取课程图片
     * @Author 邓元粮
     * @MethodName findCoursePic
     * @Date 10:10 2019/6/8
     * @Param [courseId]
     * @return com.xuecheng.framework.domain.course.CoursePic
     **/
    public CoursePic findCoursePic(String courseId) {
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null ;
    }

    /**
     * 删除课程id和图片路径绑定关系
     * @Author 邓元粮
     * @MethodName deleteCoursePic
     * @Date 15:57 2019/6/8
     * @Param [courseId]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        long delNum = coursePicRepository.deleteByCourseid(courseId);
        if(delNum >0 ){
            return new ResponseResult(CommonCode.SUCCESS);
        }else{
            return new ResponseResult(CommonCode.FAIL);
        }
    }
}
