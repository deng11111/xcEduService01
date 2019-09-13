package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ValidationException;
import java.io.*;
import java.util.*;

/**
 * 上传文件业务层
 *
 * @ClassName MediaUpload
 * @Author 邓元粮
 * @Date 2019/8/26 20:22
 * @Version 1.0
 **/
@Service
public class MediaUploadService {

    private static  final Logger LOGGER = LoggerFactory.getLogger(MediaUploadService.class);
    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    private String UPLOAD_LOCATION;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //视频处理路由
    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    public  String routingkey_media_video;
    /*
     *@Description 校验文件是否已经存在,如不存在,则创建文件夹
     * @Author 邓元粮
     * @MethodName register
     * @Datetime 20:25 2019/8/26
     * @Param [fileMd5, fileName, fileSize, mimetype, fileExt]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //校验磁盘是否已存在
        String filePath = getFilePath(fileMd5, fileExt);
        String fileFolderPath = getFileFolderPath(fileMd5);
        File file = new File(filePath);
        boolean fileExists = file.exists();
        //校验mongoDB是否存在
        Optional<MediaFile> mediaFileOptional = mediaFileRepository.findById(fileMd5);
        if (fileExists && mediaFileOptional.isPresent()) {//文件已存在
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /*
     *@Description 校验分块是否存在
     * @Author 邓元粮
     * @MethodName checkchunk
     * @Datetime 21:08 2019/8/26
     * @Param [fileMd5, chunk, chunkSize]
     * @return com.xuecheng.framework.domain.media.response.CheckChunkResult
     **/
    public CheckChunkResult checkchunk(String fileMd5, String chunk, Long chunkSize) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File chunkFile = new File(chunkFileFolderPath + chunk);
        if (chunkFile.exists()) {
            return new CheckChunkResult(CommonCode.SUCCESS, true);
        } else {
            return new CheckChunkResult(CommonCode.SUCCESS, false);

        }
    }

    /*
     *@Description 获取文件夹路径
     * @Author 邓元粮
     * @MethodName getFileFolderPath
     * @Datetime 20:49 2019/8/26
     * @Param [fileMd5]
     * @return java.lang.String
     **/
    private String getFileFolderPath(String fileMd5) {
        return UPLOAD_LOCATION + "/" + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
    }

    /*
     *@Description 获取分块路径
     * @Author 邓元粮
     * @MethodName getChunkFileFolderPath
     * @Datetime 21:10 2019/8/26
     * @Param [fileMd5, chunk]
     * @return java.lang.String
     **/
    private String getChunkFileFolderPath(String fileMd5) {
        return UPLOAD_LOCATION + "/" + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/chunks/";
    }

    /*
     *@Description 获取文件路径
     * @Author 邓元粮
     * @MethodName getFilePath
     * @Datetime 20:48 2019/8/26
     * @Param [fileMd5, fileExt]
     * @return java.lang.String
     **/
    private String getFilePath(String fileMd5, String fileExt) {
        return UPLOAD_LOCATION + "/" + fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
    }

    /*
     *@Description 上传分块
     * @Author 邓元粮
     * @MethodName uploadchunk
     * @Datetime 21:13 2019/8/26
     * @Param [file, chunk, fileMd5]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5) {
        if (file == null) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_ISNULL);
        }
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            inputStream = file.getInputStream();
            String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
            File fileFolder = new File(chunkFileFolderPath);
            if (!fileFolder.exists()) {
                fileFolder.mkdirs();
            }
            File chunkFile = new File(chunkFileFolderPath + chunk);
            if (!chunkFile.exists()) {
                chunkFile.createNewFile();
            }
            outputStream = new FileOutputStream(chunkFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ValidationException("上传文件失败!");
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ValidationException("上传文件失败!");
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ValidationException("上传文件失败!");
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /*
     *@Description 合并文件
     * @Author 邓元粮
     * @MethodName mergechunks
     * @Datetime 22:01 2019/8/27
     * @Param [fileMd5, fileName, fileSize, mimetype, fileExt]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    @Transactional
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        try {
            //合并文件
            String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
            File chunkFileFolder = new File(chunkFileFolderPath);
            File[] chunkFiles = chunkFileFolder.listFiles();
            List<File> chunkFileList = Arrays.asList(chunkFiles);
            String merge_file_path = getFilePath(fileMd5, fileExt);
            File merge_file = new File(merge_file_path);
            merge_file = mergeFile(chunkFileList, merge_file);
            if (merge_file == null) {
                ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
            }
            //校验文件
            boolean result = checkFileMd5(merge_file, fileMd5);
            if (!result) {
                ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
            }
            //信息入库
            MediaFile mediaFile = new MediaFile();
            mediaFile.setFileId(fileMd5);
            mediaFile.setFileName(fileMd5 + "." + fileExt);
            mediaFile.setFileOriginalName(fileName);
            //文件路径保存相对路径
            mediaFile.setFilePath(getFileFolderRelativePath(fileMd5, fileExt));
            mediaFile.setFileSize(fileSize);
            mediaFile.setUploadTime(new Date());
            mediaFile.setMimeType(mimetype);
            mediaFile.setFileType(fileExt);
            //状态为上传成功
            mediaFile.setFileStatus("301002");
            MediaFile save = mediaFileRepository.save(mediaFile);
            sendProcessVideoMsg(save.getFileId());
            return new ResponseResult(CommonCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(MediaCode.MERGE_FILE_CHECKFAIL);
        }
    }

    /*
     *@Description 发送视频处理消息
     * @Author 邓元粮
     * @MethodName sendProcessVideoMsg
     * @Datetime 7:33 2019/9/3
     * @Param [mediaId]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/
    public ResponseResult sendProcessVideoMsg(String mediaId){
        Optional<MediaFile> mediaFileOptional = mediaFileRepository.findById(mediaId);
        if (!mediaFileOptional.isPresent()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        Map<String, String> messageMap = new HashMap<String,String>();
        messageMap.put("mediaId", mediaId);
        String message = JSON.toJSONString(messageMap);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,message);
        } catch (AmqpException e) {
            e.printStackTrace();
            LOGGER.info("send media process task error,msg is:{},error:{}", message,e.getMessage());
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /*
     *@Description 获取文件相对路径
     * @Author 邓元粮
     * @MethodName getFileFolderRelativePath
     * @Datetime 22:27 2019/8/27
     * @Param [fileMd5, fileExt]
     * @return java.lang.String
     **/
    private String getFileFolderRelativePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/"+fileMd5+"/";
    }

    /*
     *@Description 校验文件md5是否跟前端传来的一致
     * @Author 邓元粮
     * @MethodName checkFileMd5
     * @Datetime 22:14 2019/8/27
     * @Param [merge_file, fileMd5]
     * @return void
     **/
    private boolean checkFileMd5(File merge_file, String fileMd5) throws IOException {
        FileInputStream inputStream = new FileInputStream(merge_file);
        String merge_md5 = DigestUtils.md5Hex(inputStream);
        return fileMd5.equalsIgnoreCase(merge_md5);
    }

    /*
     *@Description 根据文件集合合并文件至merge_file
     * @Author 邓元粮
     * @MethodName mergeFile
     * @Datetime 22:07 2019/8/27
     * @Param [chunkFileList, merge_file]
     * @return java.io.File
     **/
    private File mergeFile(List<File> chunkFileList, File merge_file) {

        try {
            if (merge_file.exists()) {
                merge_file.delete();
            } else {
                merge_file.createNewFile();
            }
            //文件排序
            if (!CollectionUtils.isEmpty(chunkFileList)) {
                Collections.sort(chunkFileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())) {
                            return 1;
                        }
                        return -1;
                    }
                });
                RandomAccessFile raf_write = new RandomAccessFile(merge_file, "rw");
                byte[] b = new byte[1024];
                for (File chunkFile : chunkFileList) {
                    RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
                    int len = -1;
                    while ((len = raf_read.read(b)) != -1) {
                        raf_write.write(b, 0, len);
                    }
                    raf_read.close();
                }
                raf_write.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return merge_file;
    }
}
