package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @ClassName TestFastDFS
 * @Author 邓元粮
 * @Date 2019/6/3 7:27
 * @Version 1.0
 **/

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {
    /**
     * 上传测试
     * @Author 邓元粮
     * @MethodName
     * @Date 7:28 2019/6/3
     * @Param
     * @return
     **/
    @Test
    public void testUpload(){
        try {
            //读取配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //创建tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //获取tracker服务
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage服务
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //获取storage客户端
            StorageClient1 storageClient = new StorageClient1(trackerServer,storageServer);
            //获取本地图片路径
            String picPath = "D:/img/red.jpg";
            //上传图片,获取图片id--即相对存储路径
            String fileId = storageClient.upload_file1(picPath, "jpg", null);
            // http://192.168.101.3/group1/M00/00/00/wKhlA1zzqnmAf4alAAEWCQQlm38040.jpg
            System.out.println("filePath : "+ fileId);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载测试
     * @Author 邓元粮
     * @MethodName testDownload
     * @Date 7:29 2019/6/3
     * @Param []
     * @return void
     **/
    @Test
    public void testDownload(){
        try {
            //读取配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //获取tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //获取tracker服务
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage服务
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建storage客户端1
            StorageClient1 storageClient = new StorageClient1(trackerServer,storageServer);
            String fileId = "group1/M00/00/00/wKhlA1zzqnmAf4alAAEWCQQlm38040.jpg";
            //下载图片
            byte[] bytes = storageClient.download_file1(fileId);
            FileOutputStream fileOutputStream = new FileOutputStream(new File("D:/img/download/Tshirt.jpg"));
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询测试
     * @Author 邓元粮
     * @MethodName testSearch
     * @Date 7:30 2019/6/3
     * @Param []
     * @return void
     **/
    @Test
    public void testSearch(){
        try {
            //读取配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //获取tracker客户端
            TrackerClient trackerClient = new TrackerClient();
            //获取连接，获取tracker服务
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取存储服务
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //获取存储客户端
            StorageClient1 storageClient = new StorageClient1(trackerServer,storageServer);
            String fileId = "group1/M00/00/00/wKhlA1zzqnmAf4alAAEWCQQlm38040.jpg";
            String groupName = "group1";
            // 注： query_file_info 这个方法无用
            FileInfo fileInfo = storageClient.query_file_info1( fileId);
            System.out.println(fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
