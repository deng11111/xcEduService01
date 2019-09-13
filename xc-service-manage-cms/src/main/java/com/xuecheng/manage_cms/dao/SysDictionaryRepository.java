package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @ClassName SysDictionaryRepository
 * @Description TODO
 * @Author Administrator
 * @Date 2019/2/1922:10
 * @Version 1.0
 **/

public interface SysDictionaryRepository extends MongoRepository<SysDictionary,String> {

    /**
     * 根据类型获取字典数据
     * @Author 邓元粮
     * @MethodName findByDType
     * @Date 22:11 2019/2/19
     * @Param [type]
     * @return com.xuecheng.framework.domain.system.SysDictionary
     **/
    public SysDictionary findByDType(String type);
}
