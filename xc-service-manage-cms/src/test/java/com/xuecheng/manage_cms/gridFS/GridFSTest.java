package com.xuecheng.manage_cms.gridFS;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;

/**
 * @ClassName GridFSTest
 * @Author 邓元粮
 * @Date 2019/1/22 22:06
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTest {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket ;


    /**
     * 存取测试
     * @Author 邓元粮
     * @MethodName testGridFsTemplate
     * @Date 22:16 2019/1/22
     * @Param []
     * @return void
     **/
    @Test
    public void testGridFsTemplate() throws FileNotFoundException {
        File content = new File("D:\\temp\\course.ftl");
        FileInputStream inputStream = new FileInputStream(content);
        ObjectId objectId = gridFsTemplate.store(inputStream, "course.ftl");
        System.out.println(objectId);//5cfcd08823cb3a2b8c3c684f
    }

    /**
     * 读取文件
     * @Author 邓元粮
     * @MethodName testGridFSBucket
     * @Date 22:29 2019/1/22
     * @Param []
     * @return void
     **/
    @Test
    public void testGridFSBucket() throws IOException {
        //查询gridFS file
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5c47244ca14d7a3d843b85c6")));
        //打开下载流
        GridFSDownloadStream stream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建resource用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,stream);
        //获取下载流
        InputStream in = gridFsResource.getInputStream();
        //读取文件
        String content = IOUtils.toString(in, "utf-8");

        System.out.println(content);
    }
}
