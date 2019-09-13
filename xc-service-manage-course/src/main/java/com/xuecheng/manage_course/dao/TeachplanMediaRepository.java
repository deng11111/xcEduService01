package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 课程计划-媒资文件绑定关系持久层
 * @ClassName TeachplanMediaRepository
 * @Description TODO
 * @Author Administrator
 * @Date 2019/9/78:44
 * @Version 1.0
 **/
public interface TeachplanMediaRepository extends JpaRepository<TeachplanMedia, String> {

    List<TeachplanMedia> findByCourseId(String courseId);
}
