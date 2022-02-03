package com.yee.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yee.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ClassName: DictService
 * Description:
 * date: 2021/12/25 10:24
 *
 * @author Yee
 * @since JDK 1.8
 */
public interface DictService extends IService<Dict> {
    //查询子数据列表
    List<Dict> findChildData(long id);
    //导出数据
    void exportData(HttpServletResponse response);
    //导入数据
    void importData(MultipartFile file);

    //获取数据字典名称
    String getName(String  parentDictCode,String value);
    //根据dictCode获取下级节点
    List<Dict> findByDictCode(String dictCode);
}
