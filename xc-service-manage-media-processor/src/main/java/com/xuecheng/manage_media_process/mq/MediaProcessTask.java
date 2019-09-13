package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.service.ApiListing;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @ClassName MediaProcessTask
 * @Author 邓元粮
 * @Date 2019/8/29 21:57
 * @Version 1.0
 **/
@Component
public class MediaProcessTask {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.ffmpeg-path}")
    private String ffmpeg_path;

    @Value("${xc-service-manage-media.video-location}")
    private String video_location;

    //指定监听队列
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    @Transactional
    public void receiveMediaProcessTask(String msg) {
        Map msgMap = JSON.parseObject(msg, Map.class);
        String mediaId = (String) msgMap.get("mediaId");
        Optional<MediaFile> mediaFileOptional = mediaFileRepository.findById(mediaId);
        if (!mediaFileOptional.isPresent()) {//沒有對象，不做任何處理
            return;
        }
        //获取mediaFile
        MediaFile mediaFile = mediaFileOptional.get();
        //是否avi格式判断
        String fileType = mediaFile.getFileType();
        if (!"avi".equals(fileType)) {
            mediaFile.setProcessStatus("303003");
            mediaFileRepository.save(mediaFile);
            return;
        } else {
            mediaFile.setProcessStatus("303001");
            mediaFileRepository.save(mediaFile);
        }
//      String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        //根据mediaFile信息将avi视频转换为MP4
        String video_path = video_location + mediaFile.getFilePath() + mediaFile.getFileName();
        String mp4_name = mediaId + ".mp4";
        String mp4_folder_path = video_location + mediaFile.getFilePath();
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_folder_path);
        String mp4Result = mp4VideoUtil.generateMp4();
        if(!"success".equals(mp4Result)){
            failedHandle(mediaFile, mp4Result);
            return ;
        }
        //MP4文件转化名m3u8
        //String ffmpeg_path, String video_path, String m3u8_name,String m3u8folder_path
        String mp4_video_path = mp4_folder_path +  mp4_name;
        String m3u8_name = mediaId + ".m3u8";
        String m3u8folder_path = mp4_folder_path + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path, mp4_video_path, m3u8_name, m3u8folder_path);
        String tsResult = hlsVideoUtil.generateM3u8();
        if(!"success".equals(tsResult)){
            failedHandle(mediaFile, tsResult);
            return;
        }
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        String fileUrl = mediaFile.getFilePath() + "hls/"+m3u8_name;
        mediaFile.setFileUrl(fileUrl);
        mediaFile.setProcessStatus("303002");
        mediaFileRepository.save(mediaFile);
    }
    
    /*
     *@Description 失败结果处理
     * @Author 邓元粮
     * @MethodName failedHandle
     * @Datetime 21:51 2019/9/2
     * @Param [mediaFile, mp4Result]
     * @return void
     **/
    private void failedHandle(MediaFile mediaFile, String mp4Result) {
        //操作失败写入处理日志
        mediaFile.setProcessStatus("303003");//处理状态为处理失败
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setErrormsg(mp4Result);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        mediaFileRepository.save(mediaFile);
    }
}
