package createworld.elasticspringboot.service;

import createworld.elasticspringboot.bean.DocBean;
import org.springframework.data.domain.Page;

import java.util.Iterator;
import java.util.List;

/**
 * @author zhengzebiao
 * @date 2020/2/19 14:15
 */
public interface IElasticService {
    void createIndex();

    void deleteIndex(String index);

    void save(DocBean docBean);

    void saveAll(List<DocBean> list);

    Iterator<DocBean> findAll();

    Page<DocBean> findByContent(String content);

    Page<DocBean> findByFirstCode(String firstCode);

    Page<DocBean> findBySecondCode(String secordCode);

    Page<DocBean> query(String key);
}
