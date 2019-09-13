package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

    @Test
    public void testProcessBuilder() throws IOException {
        //构建进程对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        //添加命令
        processBuilder.command("ping", "127.0.0.1");
        //报错信息合并
        processBuilder.redirectErrorStream(true);
        //执行命令
        Process process = processBuilder.start();
        //获取输出数据
        InputStream inputStream = process.getInputStream();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"gbk");
        char[] chars = new char[1024];
        int len = -1;
        while ((len = inputStreamReader.read(chars)) != -1){
            String outStr = new String(chars, 0, len);
            System.err.println(outStr);
        }
    }

    //测试使用工具类将avi转成mp4
    @Test
    public void testProcessMp4() throws IOException {
        //构建进程对象
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> command = new ArrayList<String>();
        command.add("D:\\software\\ffmpeg\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("D:\\software\\ffmpeg\\ffmpegtest\\solr.avi");
        command.add("-y");//覆盖输出文件
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("D:\\software\\ffmpeg\\ffmpegtest\\solr.mp4");
        //添加命令
        processBuilder.command(command);
        //报错信息合并
        processBuilder.redirectErrorStream(true);
        //执行命令
        Process process = processBuilder.start();
        //获取输出数据
        InputStream inputStream = process.getInputStream();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"gbk");
        char[] chars = new char[1024];
        int len = -1;
        while ((len = inputStreamReader.read(chars)) != -1){
            String outStr = new String(chars, 0, len);
            System.err.println(outStr);
        }
        inputStream.close();
        inputStreamReader.close();
    }

    @Test
    public void testMp4Util(){
        String ffmpeg_path = "D:\\software\\ffmpeg\\bin\\ffmpeg.exe";
        String video_path = "D:\\software\\ffmpeg\\ffmpegtest\\solr.avi";
        String mp4_name = "solr.mp4";
        String mp4Folder_path = "D:\\software\\ffmpeg\\ffmpegtest\\";
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4Folder_path);
        String m = mp4VideoUtil.generateMp4();
        System.out.println(m);
    }

}
