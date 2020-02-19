package createworld.elasticspringboot.dao;

import createworld.elasticspringboot.bean.DocBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author zhengzebiao
 * @date 2020/2/19 14:12
 */
public interface ElasticRepository extends ElasticsearchRepository<DocBean, Long> {

//    @Query("{\"bool\" : {\"must\" : {\"field\" : {\"content\" : \"?\"}}}}")
    Page<DocBean> findByContent(String content, Pageable pageable);

    Page<DocBean> findByFirstCode(String firstCode,Pageable pageable);

    Page<DocBean> findBySecondCode(String second,Pageable pageable);
}
