package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 媒资处理接口,新增、修改、删除等操作
 * @ClassName MediaFileControllerApi
 * @Description TODO
 * @Author Administrator
 * @Date 2019/9/322:06
 * @Version 1.0
 **/
@Api(value = "媒资文件处理接口",description = "媒资管理处理接口",tags = {"媒资管理接口"})
public interface MediaFileControllerApi {
    @ApiOperation("查询文件列表")
    public QueryResponseResult findList(Integer page, Integer size, QueryMediaFileRequest queryMediaFileRequest);
}
