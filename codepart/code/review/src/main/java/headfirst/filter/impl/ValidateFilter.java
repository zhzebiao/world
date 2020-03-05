package headfirst.filter.impl;

import headfirst.filter.BaseData;
import headfirst.filter.BaseFilter;

import java.util.*;

/**
 * 3
 *
 * @author zhengzebiao
 * @date 2020/2/20 10:54
 */
public class ValidateFilter implements BaseFilter {

    @Override
    public BaseData filter(BaseData baseData) {
        Iterator<Map<String, Object>> content = baseData.getContent();
        while (content.hasNext()) {
            Map<String, Object> record = content.next();
            try {
                Integer tagValue = (Integer) record.get("tag");
                if (tagValue == 1) {
                    content.remove();
                }
            } catch (Exception e) {
                content.remove();
            }
        }
        return baseData;
    }

    public static void main(String[] args) {
        BaseData baseData = new BaseData();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int j = 0; j < 10; j++) {
            Map<String, Object> map = new HashMap<>();
            map.put("tag", j);
            list.add(map);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("tag","123");
        list.add(map);
        baseData.setContent(list);
        ValidateFilter filter = new ValidateFilter();
        Iterator<Map<String, Object>> content = baseData.filter(filter).getContent();

        baseData.filter(filter).filter(filter).filter(filter);
        while (content.hasNext()) {
            System.out.println(content.next().get("tag"));
        }
    }
}