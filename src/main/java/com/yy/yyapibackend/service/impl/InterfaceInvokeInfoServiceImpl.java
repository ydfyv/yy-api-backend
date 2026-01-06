package com.yy.yyapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.yyapibackend.model.entity.InterfaceInvokeInfo;
import com.yy.yyapibackend.service.InterfaceInvokeInfoService;
import com.yy.yyapibackend.mapper.InterfaceInvokeInfoMapper;
import org.springframework.stereotype.Service;

/**
 * @author 阿狸
 */
@Service
public class InterfaceInvokeInfoServiceImpl extends ServiceImpl<InterfaceInvokeInfoMapper, InterfaceInvokeInfo> implements InterfaceInvokeInfoService {

    @Override
    public boolean increaseInvokeCount(Long userId, Long interfaceInfoId) {
        return lambdaUpdate().eq(InterfaceInvokeInfo::getUserId, userId)
                .eq(InterfaceInvokeInfo::getInterfaceInfoId, interfaceInfoId)
                .setSql("count = count + 1").update();
    }
}




