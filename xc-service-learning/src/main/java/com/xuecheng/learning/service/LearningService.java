package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.response.GetMediaResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.learning.client.CourseSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学习中心服务层
 * @ClassName LearningService
 * @Author 邓元粮
 * @Date 2019/9/8 16:19
 * @Version 1.0
 **/
@Service
public class LearningService {
    @Autowired
    private CourseSearchClient courseSearchClient;

    public GetMediaResult getMedia(String courseId, String teachplanId){
        //权限校验
        //获取播放地址
        TeachplanMediaPub teachplanMediaPub = courseSearchClient.getmedia(teachplanId);

        if (null != teachplanMediaPub){
            String fileUrl = teachplanMediaPub.getMediaUrl();
            return new GetMediaResult(CommonCode.SUCCESS, fileUrl);
        }
        return new GetMediaResult(CommonCode.FAIL,"" );
    }
}
