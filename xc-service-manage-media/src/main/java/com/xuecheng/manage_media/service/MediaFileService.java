package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @ClassName MediaFileService
 * @Author 邓元粮
 * @Date 2019/9/3 22:12
 * @Version 1.0
 **/
@Service
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    /*
     *@Description 分页查询文件列表数据
     * @Author 邓元粮
     * @MethodName findList
     * @Datetime 22:14 2019/9/3
     * @Param [page, size, queryMediaFileRequest]
     * @return com.xuecheng.framework.model.response.QueryResponseResult
     **/
    public QueryResponseResult findList(Integer page, Integer size, QueryMediaFileRequest queryMediaFileRequest) {
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        page = page == null ? 0 : page -1;
        size = size == null ? 1 : size;
        PageRequest pageRequest = PageRequest.of(page, size);
        MediaFile mediaFile = new MediaFile();
        if (StringUtils.isNotBlank(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotBlank(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        if (StringUtils.isNotBlank(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        //构建查询字段以及查询方式
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains());
        //组合查询参数和查询字段以及查询方式
        Example<MediaFile> mediaFileExample = Example.of(mediaFile, exampleMatcher);

        Page<MediaFile> mediaFilePage = mediaFileRepository.findAll(mediaFileExample, pageRequest);
        QueryResult<MediaFile> queryResult = new QueryResult<MediaFile>();
        if (null != mediaFilePage){
            queryResult.setTotal(mediaFilePage.getTotalElements());
            queryResult.setList(mediaFilePage.getContent());
        }else{
            queryResult.setTotal(0);
            queryResult.setList(new ArrayList<MediaFile>());
        }
        QueryResponseResult<MediaFile> mediaFileQueryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS, queryResult);
        return mediaFileQueryResponseResult;

    }
}
