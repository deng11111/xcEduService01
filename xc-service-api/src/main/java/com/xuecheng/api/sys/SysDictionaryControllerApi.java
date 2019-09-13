package com.xuecheng.api.sys;

import com.xuecheng.framework.domain.system.SysDictionary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @ClassName SysDicthinaryControllerApi
 * @Author 邓元粮
 * @Date 2019/2/19 21:44
 * @Version 1.0
 **/

@Api(value = "数据字典接口",description = "提供数据字典接口的管理、查询功能")
public interface SysDictionaryControllerApi {

    //数据字典
    @ApiOperation(value="数据字典查询接口")
    public SysDictionary getByType(String type);

}
