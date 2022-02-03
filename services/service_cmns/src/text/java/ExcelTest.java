import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: ExcelTest
 * Description:
 * date: 2021/12/25 14:53
 * excel读写测试类
 * @author Yee
 * @since JDK 1.8
 */
public class ExcelTest {
    public static void main(String[] args) {
        //写操作
//        String fileName = "F:\\temp\\test.xlsx";
//        EasyExcel.write(fileName,Stu.class).sheet("标签名随意")
//                .doWrite(data());
        //读操作
        String fileName = "F:\\temp\\test.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName,Stu.class,new ExcelListener()).sheet().doRead();
    }

    //准备写的数据
    private static List<Stu> data() {
        List<Stu> list = new ArrayList<Stu>();
        for (int i = 0; i < 10; i++) {
            Stu data = new Stu();
            data.setSno(i);
            data.setSname("张三"+i);
            list.add(data);
        }
        return list;
    }
}
