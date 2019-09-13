package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName CourseMarketRepository
 * @Author 邓元粮
 * @Date 2019/5/12 17:10
 * @Version 1.0
 **/
public interface CourseMarketRepository extends JpaRepository<CourseMarket,String> {
}
