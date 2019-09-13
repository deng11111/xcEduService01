package com.xuecheng.learning.client;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName CourseSearchClient
 * @Description TODO
 * @Author Administrator
 * @Date 2019/9/816:07
 * @Version 1.0
 **/
@FeignClient(value = XcServiceList.XC_SERVICE_SEARCH)
public interface CourseSearchClient {

    /*
     *@Description 根据课程计划id获取课程计划媒资文件绑定信息,主要为了播放地址
     * @Author 邓元粮
     * @MethodName getmedia
     * @Datetime 16:08 2019/9/8
     * @Param [teachplanId]
     * @return com.xuecheng.framework.domain.course.TeachplanMediaPub
     **/
    @GetMapping(value="/search/course/getmedia/{teachplanId}")
    TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);

}
