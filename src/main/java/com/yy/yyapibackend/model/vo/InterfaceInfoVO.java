package com.yy.yyapibackend.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.yy.yyapimodel.model.entity.InterfaceInfo;
import lombok.Data;

import java.util.Date;

@Data
public class InterfaceInfoVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 请求方法 (GET、POST。。。)
     */
    private String method;

    /**
     * 创建用户 id
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public static InterfaceInfoVO objToVo(InterfaceInfo interfaceInfo) {
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        BeanUtil.copyProperties(interfaceInfo, interfaceInfoVO);
        return interfaceInfoVO;
    }
}
