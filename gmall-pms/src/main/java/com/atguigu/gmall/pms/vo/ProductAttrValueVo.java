package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public class ProductAttrValueVo extends ProductAttrValueEntity {
    public void setValueSelected(List<String> valueSelected){
        this.setAttrValue(StringUtils.join(valueSelected,","));
    }
}
