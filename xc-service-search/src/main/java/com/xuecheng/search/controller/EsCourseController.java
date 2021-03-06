package com.xuecheng.search.controller;
import com.xuecheng.api.search.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 课程搜索类
 * @ClassName EsCourseController
 * @Author 邓元粮
 * @Date 2019/8/2 7:56
 * @Version 1.0
 **/
@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {

    @Autowired
    private EsCourseService esCourseService;

    @Override
    @GetMapping(value="/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") Integer page,@PathVariable("size") Integer size, CourseSearchParam courseSearchParam) throws IOException {
        page = page == null ? 1 : page;
        size = size == null ? 25 : size;
        return esCourseService.list(page,size,courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getall(@PathVariable("id") String id) {

        return esCourseService.getall(id);
    }

    @Override
    @GetMapping(value="/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable String teachplanId) {
        QueryResponseResult<TeachplanMediaPub> queryResponseResult = esCourseService.getmedia(new String[]{teachplanId});
        if (null != queryResponseResult){
            QueryResult<TeachplanMediaPub> queryResult = queryResponseResult.getQueryResult();
            if (queryResult != null){
                List<TeachplanMediaPub> resultList = queryResult.getList();
                if (!CollectionUtils.isEmpty(resultList)){
                    return resultList.get(0);
                }
            }
        }
        return new TeachplanMediaPub();
    }
}
