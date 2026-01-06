package com.yy.yyapibackend.service;

import com.yy.yyapibackend.model.entity.InterfaceInvokeInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 阿狸
 */
public interface InterfaceInvokeInfoService extends IService<InterfaceInvokeInfo> {
    boolean increaseInvokeCount(Long userId, Long interfaceInfoId);
}
