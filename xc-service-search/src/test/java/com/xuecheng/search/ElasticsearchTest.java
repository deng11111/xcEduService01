package com.xuecheng.search;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName search
 * @Author 邓元粮
 * @Date 2019/6/17 7:53
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    private RestHighLevelClient highLevelClient;

    @Autowired
    private RestClient restClient;

    @Test
    public void testDelete(){
        try {
        //创建删除请求
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        //创建索引客户端
        IndicesClient indices = highLevelClient.indices();
        //删除索引库
        DeleteIndexResponse delete = null;
            delete = indices.delete(deleteIndexRequest);
        System.out.println(delete.isAcknowledged());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreate(){
        try {
        //获取创建索引库请求
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        //设置分片,及副本
        createIndexRequest.settings(Settings.builder().put("number_of_shards","1").put("number_of_replicas","0"));
        //设置映射
        createIndexRequest.mapping("doc","{\n" +
                "\"properties\": {\n" +
                "\"name\": {\n" +
                "\"type\": \"text\",\n" +
                "\"analyzer\":\"ik_max_word\",\n" +
                "\"search_analyzer\":\"ik_smart\"\n" +
                "},\n" +
                "\"description\": {\n" +
                "\"type\": \"text\",\n" +
                "\"analyzer\":\"ik_max_word\",\n" +
                "\"search_analyzer\":\"ik_smart\"\n" +
                "},\n" +
                "\"studymodel\": {\n" +
                "\"type\": \"keyword\"\n" +
                "},\n" +
                "\"price\": {\n" +
                "\"type\": \"float\"\n" +
                "},\n" +
                "\"timestamp\": {\n" +
                "\"type\": \"date\",\n" +
                "\"format\": \"yyyy‐MM‐dd HH:mm:ss||yyyy‐MM‐dd||epoch_millis\"\n" +
                "}\n" +
                "}\n" +
                "}", XContentType.JSON);
            IndicesClient indices = highLevelClient.indices();
            CreateIndexResponse create = indices.create(createIndexRequest);
            System.out.println(create.isShardsAcknowledged());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddDoc() throws IOException {
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);

        IndexRequest indexRequest = new IndexRequest("xc_course","doc");
        indexRequest.source(jsonMap);
        IndexResponse add = highLevelClient.index(indexRequest);
        System.out.println(add.getResult());

    }

    @Test
    public void testQueryDoc() throws  Exception{
        GetRequest getIndexRequest = new GetRequest("xc_course","doc","X_f3bGsBGcTYz7Qs0Gx9");
        GetResponse get = highLevelClient.get(getIndexRequest);
        boolean exists = get.isExists();
        if (exists){
            Map<String, Object> sourceAsMap = get.getSourceAsMap();
            System.out.println(sourceAsMap);
        }
    }

    @Test
    public void testUpdateDoc() throws  Exception {
        UpdateRequest updateRequest = new UpdateRequest("xc_course","doc","X_f3bGsBGcTYz7Qs0Gx9");
        Map<String, String> map = new HashMap<>();
        map.put("name", "spring cloud实战222222");
        updateRequest.doc(map);
        UpdateResponse update = highLevelClient.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }

    @Test
    public void testDeleteDoc() throws  Exception {
        DeleteRequest deleteRequest = new DeleteRequest("xc_course","doc","X_f3bGsBGcTYz7Qs0Gx9");
        DeleteResponse delete = highLevelClient.delete(deleteRequest);
        RestStatus status = delete.status();
        System.out.println(status);
    }

}
