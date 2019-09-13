package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理费服务
 * @ClassName FileSystemControllerApi
 * @Author 邓元粮
 * @Date 2019/6/7 6:32
 * @Version 1.0
 **/
@Api(value = "文件管理",tags = "文件管理",description = "文件管理,提供文件上传,下载,删除操作")
public interface FileSystemControllerApi {
    
    /**
     * 
     * @Author 邓元粮
     * @MethodName upload
     * @Date 7:06 2019/6/7
     * @Param [multipartFile, filetag, businesskey, metadata]
     * @return com.xuecheng.framework.domain.filesystem.response.UploadFileResult
     **/
    @ApiOperation("上传文件")
    UploadFileResult upload(MultipartFile multipartFile,
                            String filetag,
                            String businesskey,
                            String metadata);


}
