package createworld.elasticspringboot.service.impl;

import createworld.elasticspringboot.bean.DocBean;
import createworld.elasticspringboot.dao.ElasticRepository;
import createworld.elasticspringboot.service.IElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * @author zhengzebiao
 * @date 2020/2/19 14:16
 */
@Service("elasticService")
public class ElasticServiceImpl implements IElasticService {


    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    @Autowired
    private ElasticRepository repository;

    private Pageable pageable = PageRequest.of(0, 10);

    @Override
    public void createIndex() {
        elasticsearchTemplate.createIndex(DocBean.class);
    }

    @Override
    public void deleteIndex(String index) {
        elasticsearchTemplate.deleteIndex(index);
    }

    @Override
    public void save(DocBean docBean) {
        repository.save(docBean);
    }

    @Override
    public void saveAll(List<DocBean> list) {
        repository.saveAll(list);
    }

    @Override
    public Iterator<DocBean> findAll() {
        return repository.findAll().iterator();
    }

    @Override
    public Page<DocBean> findByContent(String content) {
        return repository.findByContent(content, pageable);
    }

    @Override
    public Page<DocBean> findByFirstCode(String firstCode) {
        return repository.findByFirstCode(firstCode, pageable);
    }

    @Override
    public Page<DocBean> findBySecondCode(String secordCode) {
        return repository.findBySecondCode(secordCode,pageable);
    }

    @Override
    public Page<DocBean> query(String key) {
        return repository.findByContent(key,pageable);
    }
}