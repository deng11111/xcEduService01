package com.xuecheng.manage_course.controller;

import com.xuecheng.api.category.CategoryControllerApi;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.manage_course.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CategoryController
 * @Author 邓元粮
 * @Date 2019/2/19 21:14
 * @Version 1.0
 **/

@RestController
@RequestMapping("/category")
public class CategoryController implements CategoryControllerApi {

    @Autowired
    private CategoryService categoryService ;

    @Override
    @GetMapping("/list")
    public CategoryNode list() {
        return categoryService.list();
    }
}
