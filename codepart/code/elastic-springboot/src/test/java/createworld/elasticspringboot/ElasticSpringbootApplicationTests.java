package createworld.elasticspringboot;

import createworld.elasticspringboot.bean.DocBean;
import createworld.elasticspringboot.service.impl.ElasticServiceImpl;
import org.apache.lucene.search.DocIdSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SpringBootTest
class ElasticSpringbootApplicationTests {

    @Autowired
    private ElasticServiceImpl service;

    @Test
    void contextLoads() {
    }

    @Test
    public void testCreateIndex() {
        service.createIndex();
    }

    @Test
    public void testSaveAll() {
        List<DocBean> list = new ArrayList<>();
        list.add(new DocBean(7L, "XX0193", "XX8064", "12 34", 1));
        list.add(new DocBean(8L, "XX0210", "XX7475", "12 345", 1));
        list.add(new DocBean(9L, "XX0257", "XX8097", "12 3456", 1));
        service.saveAll(list);
    }

    @Test
    public void testFindAll() {
        Iterator<DocBean> iterator = service.findAll();
        while (iterator.hasNext()) {
            System.out.println("Record :" + iterator.next());
        }
    }
    @Test
    public void testDelete(){
        service.deleteIndex("test");
    }

    @Test
    public void testFindByFirstCode(){
        Page<DocBean> page =  service.findByFirstCode("XX0193");
        Iterator<DocBean> items = page.iterator();
        while(items.hasNext()){
            System.out.println(items.next());
        }
    }
    @Test
    public void testFindByContent(){
        Page<DocBean> page =  service.findByContent("12");
        Iterator<DocBean> items = page.iterator();
        while(items.hasNext()){
            System.out.println(items.next());
        }
    }

}
