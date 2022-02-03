package com.yee.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yee.yygh.cmn.listener.DictListener;
import com.yee.yygh.cmn.mapper.DictMapper;
import com.yee.yygh.cmn.service.DictService;
import com.yee.yygh.common.handler.YyghException;
import com.yee.yygh.model.cmn.Dict;
import com.yee.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: DictServiceImpl
 * Description:
 * date: 2021/12/25 10:24
 *
 * @author Yee
 * @since JDK 1.8
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    //注入监听器,直接new会直接报错
    @Autowired
    private DictListener dictListener;


    /**
     * 先查询redis缓冲
     * @param id  父id
     * @return
     *   k=dict::selectIndexList+id  v=返回的值
     */
    @Override
    @Cacheable(value =  "dict",key = "'selectIndexList'+#id")
    public List<Dict> findChildData(long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper();
        wrapper.eq("parent_id",id);
        List<Dict> list = baseMapper.selectList(wrapper);
        for (Dict dict : list) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return list;
    }

    //导出数据
    @Override
    public void exportData(HttpServletResponse response) {
        try {
            //设置参数
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
            //查询导出数据,null表示没有查询条件
            List<Dict> list = baseMapper.selectList(null);
            List<DictEeVo> dictEeVos = new ArrayList<>();
            //数据类型转换
            for (Dict dict : list) {
                DictEeVo dictVo = new DictEeVo();
                //工具复制属性值,适合字段一致的情况
                BeanUtils.copyProperties(dict,dictVo);
                dictEeVos.add(dictVo);
            }
            //写出文件
            EasyExcel.write(response.getOutputStream(), DictEeVo.class)
            .sheet("字典数据").doWrite(dictEeVos);
        } catch (IOException e) {
            e.printStackTrace();
            throw  new YyghException(20001,"导出数据失败");
        }


    }

    //导入数据
    @Override
    public void importData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,dictListener)
                    .sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
            throw new YyghException(20001,"导入数据失败");
        }
    }

    //获取数据字典名称
    @Override
    public String getName(String  parentDictCode,String value) {
        //初始化
        Dict dict = null;
        String name = "";
        //查询条件
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("value",value);
        //不为空说明查询医院字段
       if (!StringUtils.isEmpty(parentDictCode)){
           //根据dict_code查询父级别数据
           Dict parentDict = this.getDictByDictCode(parentDictCode);
           wrapper.eq("parent_id",parentDict.getId());
       }
        dict  = baseMapper.selectOne(wrapper);
        if (dict != null){
            name = dict.getName();
        }
       return name;
    }
    //根据dictCode获取下级节点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //1、根据dict_code查询
        Dict byDictCode = this.getDictByDictCode(dictCode);
        //根据父id查询
        return this.findChildData(byDictCode.getId());
    }

    //根据dict_code查询父级别数据
    private Dict getDictByDictCode(String parentDictCode) {
        //查询条件
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("dict_code",parentDictCode);
        return baseMapper.selectOne(wrapper);
    }

    //判断是否有子数据
    private boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
       return count>0;
    }

}
