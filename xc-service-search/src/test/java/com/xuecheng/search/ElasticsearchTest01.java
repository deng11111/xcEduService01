package com.xuecheng.search;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @ClassName ElasticsearchTest01
 * @Author 邓元粮
 * @Date 2019/6/20 6:54
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest01 {
    @Autowired
    private RestHighLevelClient highLevelClient;

    @Autowired
    private RestClient restClient;

    @Test
    public void  testAllQuery() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询方式,此处使用查询所有
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1)*size;
        searchSourceBuilder.from(from);//起始记录下标，从0凯斯
        searchSourceBuilder.size(size);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //获取总记录数
        long hitsTotal = searchHits.getTotalHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);

    }
    /**
     * termQuery精确查询
     * @Author 邓元粮
     * @MethodName testTermQuery
     * @Date 7:59 2019/6/20
     * @Param []
     * @return void
     **/
    @Test
    public void  testTermQuery() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询方式,此处使用查询所有
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1)*size;
        searchSourceBuilder.from(from);//起始记录下标，从0凯斯
        searchSourceBuilder.size(size);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //获取总记录数
        long hitsTotal = searchHits.getTotalHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);
    }

    /**
     * id精确查询
     * @Author 邓元粮
     * @MethodName testTermQuery
     * @Date 7:59 2019/6/20
     * @Param []
     * @return void
     **/
    @Test
    public void  testIdsQuery() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询方式,此处使用查询所有
        String[] ids = new String[]{"1","2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",ids));
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);

        //设置分页参数
        int page = 1;
        int size = 2;
        int from = (page - 1)*size;
        searchSourceBuilder.from(from);//起始记录下标，从0凯斯
        searchSourceBuilder.size(size);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //获取总记录数
        long hitsTotal = searchHits.getTotalHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);
    }

    /**
     * match查询
     * @Author 邓元粮
     * @MethodName testTermQuery
     * @Date 7:59 2019/6/20
     * @Param []
     * @return void
     **/
    @Test
    public void  testMatchQuery() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询方式,此处使用查询所有
        String[] ids = new String[]{"1","2"};
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发框架").minimumShouldMatch("80%"));
//        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发").operator(Operator.OR));
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);

        //设置分页参数
        int page = 1;
        int size = 10;
        int from = (page - 1)*size;
        searchSourceBuilder.from(from);//起始记录下标，从0凯斯
        searchSourceBuilder.size(size);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //获取总记录数
        long hitsTotal = searchHits.getTotalHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);
    }
    /**
     * multi查询
     * @Author 邓元粮
     * @MethodName testTermQuery
     * @Date 7:59 2019/6/20
     * @Param []
     * @return void
     **/
    @Test
    public void  testMultiMatchQuery() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询方式,此处使用查询所有
        String[] ids = new String[]{"1","2"};
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name","description").minimumShouldMatch("50%").field("name",10f));
//        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发").operator(Operator.OR));
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);

        //设置分页参数
        int page = 1;
        int size = 10;
        int from = (page - 1)*size;
        searchSourceBuilder.from(from);//起始记录下标，从0凯斯
        searchSourceBuilder.size(size);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //获取总记录数
        long hitsTotal = searchHits.getTotalHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);
    }

    /**
     * 布尔查询
     * @Author 邓元粮
     * @MethodName testTermQuery
     * @Date 7:59 2019/6/20
     * @Param []
     * @return void
     **/
    @Test
    public void  testBooleanQuery() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询方式,此处使用查询所有
        String[] ids = new String[]{"1","2"};
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description").minimumShouldMatch("50%").field("name", 10f);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
//        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发").operator(Operator.OR));
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic","description"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);

        //设置分页参数
        int page = 1;
        int size = 10;
        int from = (page - 1)*size;
        searchSourceBuilder.from(from);//起始记录下标，从0凯斯
        searchSourceBuilder.size(size);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //获取总记录数
        long hitsTotal = searchHits.getTotalHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);
    }

    /**
     * 布尔查询
     * @Author 邓元粮
     * @MethodName testTermQuery
     * @Date 7:59 2019/6/20
     * @Param []
     * @return void
     **/
    @Test
    public void  testFilter() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置查询方式,此处使用查询所有
        String[] ids = new String[]{"1","2"};
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring框架", "name", "description").minimumShouldMatch("50%").field("name", 10f);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.filter(termQueryBuilder);
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").lte(100).gte(60);
        boolQueryBuilder.filter(rangeQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
//        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发").operator(Operator.OR));
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic","description"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);

        //设置分页参数
        int page = 1;
        int size = 10;
        int from = (page - 1)*size;
        searchSourceBuilder.from(from);//起始记录下标，从0凯斯
        searchSourceBuilder.size(size);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //获取总记录数
        long hitsTotal = searchHits.getTotalHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);
    }


    /**
     * 排序
     * @Author 邓元粮
     * @MethodName testTermQuery
     * @Date 7:59 2019/6/20
     * @Param []
     * @return void
     **/
    @Test
    public void  testSort() throws IOException {
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定type
        searchRequest.types("doc");
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发",
                "name", "description");
        //设置过滤字段
        String[] includes = new String[]{"name","studymodel","price", "timestamp","pic","description"};//要显示的字段
        String[] excludes = new String[]{};//不要显示的字段
        searchSourceBuilder.fetchSource(includes,excludes);
        //请求，条件绑定
        searchRequest.source(searchSourceBuilder);
        // 必须有查询条件,否则不知道那些关键字需要加标签,类似下面的这步骤一定要有
        searchSourceBuilder.query(multiMatchQueryBuilder);
        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");
        highlightBuilder.postTags("</tag>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //执行查询,返回结果集
        SearchResponse searchResponse = highLevelClient.search(searchRequest);
        //获取结果
        SearchHits searchHits = searchResponse.getHits();
        //真正结果集
        SearchHit[] hits = searchHits.getHits();
        printArr(hits);
    }

    @Test
    public void testHighlight() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","description"},
                new String[]{});
        searchRequest.source(searchSourceBuilder);
        //匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("开发",
                "name", "description");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");//设置前缀
        highlightBuilder.postTags("</tag>");//设置后缀
        // 设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        // highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);

        SearchResponse searchResponse = highLevelClient.search(searchRequest);

        SearchHits hits = searchResponse.getHits();

        SearchHit[] searchHits = hits.getHits();

        for (SearchHit hit : searchHits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //名称
            String name = (String) sourceAsMap.get("name");
            //取出高亮字段内容
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField nameField = highlightFields.get("name");
                if(nameField!=null){
                    Text[] fragments = nameField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    }
                    name = stringBuffer.toString();
                }
            }
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }

    private void printArr(SearchHit[] hits) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < hits.length; i++) {
            SearchHit hit = hits[i];
            //转map
            Map<String, Object> map = hit.getSourceAsMap();
            String name = (String) map.get("name");
            String studymodel = (String) map.get("studymodel");
            Double price = (Double) map.get("price");
            String timestamp = (String) map.get("timestamp");
            String description = (String) map.get("description");
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField nameHighlight = highlightFields.get("name");
            if (nameHighlight != null){
                Text[] fragments = nameHighlight.getFragments();
                if (null != fragments){
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < fragments.length; j++) {
                        sb.append(fragments[j].toString());
                    }
                    name = sb.toString();
                }
            }
            System.out.println(name);
            System.out.println(price);
            System.out.println(studymodel);
            System.out.println(timestamp);
            System.out.println(description);
            System.out.println("======================================================================");

        }
    }


}
