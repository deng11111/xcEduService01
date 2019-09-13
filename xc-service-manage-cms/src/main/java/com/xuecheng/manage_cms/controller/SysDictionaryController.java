package com.xuecheng.manage_cms.controller;



import com.xuecheng.api.sys.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.impl.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SysDicthinary
 * @Author 邓元粮
 * @Date 2019/2/19 22:01
 * @Version 1.0
 **/
@RestController
@RequestMapping("/sys")
public class SysDictionaryController implements SysDictionaryControllerApi {
    @Autowired
    private SysDictionaryService sysDictionaryService ;

    @Override
    @GetMapping("/dictionary/get/{type}")
    public SysDictionary getByType(@PathVariable("type") String type) {

        return sysDictionaryService.getByType(type);
    }
}
