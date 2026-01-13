package com.yy.yyapibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.yyapibackend.model.entity.InterfaceInvokeInfo;
import com.yy.yyapibackend.model.vo.InterfaceInvokeVO;
import com.yy.yyapibackend.service.InterfaceInvokeInfoService;
import com.yy.yyapibackend.mapper.InterfaceInvokeInfoMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yy.yyapibackend.constant.CommonConstant.BASE_PATH;
import static com.yy.yyapibackend.constant.CommonConstant.GATEWAY_URL;

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

    @Override
    public List<InterfaceInvokeVO> getTopInvokeInterface(Integer top) {
        List<InterfaceInvokeVO> topInvokeInterface = getBaseMapper().getTopInvokeInterface(top);

        long sum = topInvokeInterface.stream().map(InterfaceInvokeVO::getInvokeCount).reduce(0, Integer::sum);

        return topInvokeInterface.stream().peek(interfaceInvokeVO -> interfaceInvokeVO.setProportion(Double.valueOf(String.format("%.2f", (double) interfaceInvokeVO.getInvokeCount() / sum * 100)))).collect(Collectors.toList());
    }
}




