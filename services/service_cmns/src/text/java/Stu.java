import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * ClassName: Stu
 * Description:
 * date: 2021/12/25 14:53
 * 读写excel测试实体类
 * @author Yee
 * @since JDK 1.8
 */
@Data
public class Stu {
    //设置表头名称
    //设置列对应的属性
    @ExcelProperty(value = "学生编号",index = 0)
    private int sno;

    //设置表头名称
    //设置列对应的属性
    @ExcelProperty(value = "学生姓名",index = 1)
    private String sname;
}
