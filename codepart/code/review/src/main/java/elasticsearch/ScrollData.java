package elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * 使用es rest api调用scroll拉取数据
 *
 * @author zhengzebiao
 * @date 2020/3/9 9:19
 */
public class ScrollData {

    public static void main(String[] args) throws IOException {
        // 获取数据库连接

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.209.141", 9200, "http")
                )
        );


        // 设置数据拉取间隔
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest = new SearchRequest("tb_motor_vehicle_virus");
        searchRequest.scroll(scroll);
        // 生成查询条件，调用Scroll Api拉取数据
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        // 第一次请求，返回首批数据+scrollId
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        while (searchHits != null && searchHits.length > 0) {
            for (SearchHit searchHit : searchHits) {
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                for (String key : sourceAsMap.keySet()) {
                    System.out.println(key + ":" + sourceAsMap.get(key));
                }
                System.out.println("+++++++++++++++++++++++++++++++++");
            }
            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
            scrollRequest.scroll(scroll);
            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();

        }

        // Clear the Scroll context once the scroll is completed
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        boolean succeeded = clearScrollResponse.isSucceeded();
        client.close();
    }
}