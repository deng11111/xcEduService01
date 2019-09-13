package com.xuecheng.manage_media.fileTest;

import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @ClassName FileTest
 * @Author 邓元粮
 * @Date 2019/8/26 17:11
 * @Version 1.0
 **/
public class FileTest {

    /*
     *@Description 文件拆分
     * @Author 邓元粮
     * @MethodName
     * @Datetime 17:11 2019/8/26
     * @Param
     * @return
     **/

    @Test
    public void testSplitFile() throws IOException {
        //获取源文件
        File sourceFile = new File("D:\\software\\ffmpeg\\ffmpegtest\\lucene.avi");
        //创建读文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //设置每块最大值 1M
        long max_size = 1 * 1024 * 1024;
        //计算分块数
        long splitNum = (long) Math.ceil(sourceFile.length() * 1.0 / max_size);
        RandomAccessFile raf_write = null;
        for (int i = 0; i < splitNum; i++) {
            //创建缓存区
            byte[] bytes = new byte[1024];
            int len = -1;
            File wFile = new File("D:\\software\\ffmpeg\\ffmpegtest\\chunks\\" + i);
            //读取文件
            raf_write = new RandomAccessFile(wFile, "rw");
            while ((len = raf_read.read(bytes)) != -1) {
                if (wFile.length() >= max_size) {
                    break;
                }
                raf_write.write(bytes,0,len);
            }
        }
        raf_read.close();

    }

    /*
     *@Description 測試合并文件
     * @Author 邓元粮
     * @MethodName
     * @Datetime 17:12 2019/8/26
     * @Param
     * @return
     **/
    @Test
    public void testMergeFile() throws IOException {
        //获取源文件
        File wFile = new File("D:\\software\\ffmpeg\\ffmpegtest\\chunks");
        //获取文件集合
        File[] files = wFile.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1 != null && o2 != null && Integer.parseInt(o1.getName()) > Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        //创建合并文件
        File merge_file = new File("D:\\software\\ffmpeg\\ffmpegtest\\merge_lucene.avi");
        if (!merge_file.exists()){
            merge_file.createNewFile();
        }
        RandomAccessFile raf_write = new RandomAccessFile(merge_file, "rw");
        for (File file :fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = raf_read.read(bytes)) != -1){
                raf_write.write(bytes,0,len);
            }
            raf_read.close();
        }
        raf_write.close();
    }
}
