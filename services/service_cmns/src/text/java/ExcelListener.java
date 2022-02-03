import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ExcelListener
 * Description:
 * date: 2021/12/25 14:58
 * 读excel必须要有监听器
 * 一行一行的读
 * @author Yee
 * @since JDK 1.8
 */
public class ExcelListener extends AnalysisEventListener<Stu> {


    //创建list集合封装最终的数据
    List<Stu> list = new ArrayList<>();

    //一行行去读内容
    @Override
    public void invoke(Stu stu, AnalysisContext analysisContext) {
        System.out.println("-----"+stu);
        list.add(stu);
    }

    //读取excel表头信息,一般不读

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        System.out.println("表头信息："+headMap);
    }

    //读取完成后执行
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
