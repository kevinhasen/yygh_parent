package com.yee.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.yee.yygh.cmn.mapper.DictMapper;
import com.yee.yygh.model.cmn.Dict;
import com.yee.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: DictListener
 * Description:
 * date: 2021/12/25 15:59
 *
 * @author Yee
 * @since JDK 1.8
 */
@Component
public class DictListener extends AnalysisEventListener<DictEeVo> {

    @Autowired
    private DictMapper dictMapper;
    //一行行去读内容
    @Override
    public void invoke(DictEeVo data, AnalysisContext context) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(data,dict);
        //逻辑删除未删除
        dict.setIsDeleted(0);
        dictMapper.insert(dict);
    }
    //读取完成后执行
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
