package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.dao.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName CategoryService
 * @Author 邓元粮
 * @Date 2019/2/19 21:17
 * @Version 1.0
 **/

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper ;

    public CategoryNode list() {
        CategoryNode list = categoryMapper.list();
        return list;
    }
}
