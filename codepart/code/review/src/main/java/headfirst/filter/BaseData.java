package headfirst.filter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author zhengzebiao
 * @date 2020/2/20 10:51
 */
public class BaseData {
    private List<Map<String,Object>> content;

    public Iterator<Map<String,Object>> getContent(){
        return content.iterator();
    }
    public BaseData filter(BaseFilter filter){
        return filter.filter(this);
    }

    public void setContent(List<Map<String,Object>> content){
        this.content = content;
    }
}