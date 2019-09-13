package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName FileSystemService
 * @Author 邓元粮
 * @Date 2019/6/7 7:10
 * @Version 1.0
 **/
@Service
public class FileSystemService {
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private Integer connect_timeout_in_seconds ;

    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private Integer network_timeout_in_seconds ;

    @Value("${xuecheng.fastdfs.charset}")
    private String charset ;

    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers ;

    @Autowired
    private FileSystemRepository fileSystemRepository ;

    @Transactional
    public UploadFileResult upload(MultipartFile mulltipartFile, String filetag, String businesskey, String metadata)  {

        if (mulltipartFile == null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //初始化fastDFS环境
        initFastDFS();

        //获取storageClient
        String fileId = upload_fsdf(mulltipartFile);
        //创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFilePath(fileId);
        fileSystem.setFileSize(mulltipartFile.getSize());
        fileSystem.setFiletag(filetag);
        fileSystem.setFileName(mulltipartFile.getOriginalFilename());
        fileSystem.setFileType(mulltipartFile.getContentType());
        if(StringUtils.isNotBlank(metadata)){
            Map map = JSON.parseObject(metadata, Map.class);
            fileSystem.setMetadata(map);
        }
        FileSystem fileSystemResult = fileSystemRepository.save(fileSystem);

        return new UploadFileResult(CommonCode.SUCCESS,fileSystemResult);
    }

    /**
     * 获取storageClient
     * @Author 邓元粮
     * @MethodName upload_fsdf
     * @Date 7:29 2019/6/7
     * @Param []
     * @return org.csource.fastdfs.StorageClient1
     **/
    private String upload_fsdf(MultipartFile mulltipartFile) {

        try {
            //获取tracerServer
            TrackerClient trackerClient = new TrackerClient();
            //获取tracker服务
            TrackerServer trackerServer = null;
            trackerServer = trackerClient.getConnection();
            //获取trackerStorage服务
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            StorageClient1 storageClient = new StorageClient1(trackerServer,storageServer);
            //获取上传字节
            byte[] fileBytes = mulltipartFile.getBytes();
            String filename = mulltipartFile.getOriginalFilename();
            String file_ext_name = filename.substring(filename.lastIndexOf(".") + 1);
            //上传文件
            String fileId = storageClient.upload_file1(fileBytes, file_ext_name, null);
            //group1/M00/00/00/wKhlA1z0ExqAMF8MAABH9R86QfM670.png
            return fileId ;
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        return null ;
    }

    /**
     * 初始化fastdfs环境
     * @Author 邓元粮
     * @MethodName initFastDFS
     * @Date 7:17 2019/6/7
     * @Param []
     * @return void
     **/
    private void initFastDFS()  {
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
