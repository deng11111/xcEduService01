package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName CourseView
 * @Author 邓元粮
 * @Date 2019/6/9 16:12
 * @Version 1.0
 **/
@Data
@ToString
@NoArgsConstructor
public class CourseView implements Serializable {
    //课程基础信息
    private CourseBase courseBase;
    //课程营销信息
    private CourseMarket courseMarket;
    //课程图片
    private CoursePic coursePic;
    //课程计划
    private TeachplanNode teachplanNode;
}
