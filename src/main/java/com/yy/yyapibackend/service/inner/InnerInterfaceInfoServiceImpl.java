package com.yy.yyapibackend.service.inner;

import com.yy.yyapibackend.common.ErrorCode;
import com.yy.yyapibackend.exception.BusinessException;
import com.yy.yyapibackend.service.InterfaceInfoService;
import com.yy.yyapibackend.service.InterfaceInvokeInfoService;
import com.yy.yyapibackend.service.UserService;
import com.yy.yyapiinterface.api.InnerInterfaceInfoService;
import com.yy.yyapimodel.model.entity.InterfaceInfo;
import com.yy.yyapimodel.model.entity.User;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author 阿狸
 * @date 2026-01-06
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private InterfaceInvokeInfoService interfaceInvokeInfoService;

    @Resource
    private UserService userService;

    @Override
    public boolean validateInterfaceAccess(String path, String method) {
        InterfaceInfo interfaceInfo = interfaceInfoService.lambdaQuery().eq(InterfaceInfo::getUrl, path).eq(InterfaceInfo::getMethod, method).one();
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的路径和请求方法");
        }
        try {
            interfaceInfoService.validateAccessible(interfaceInfo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean increaseInvokeCount(String accessKey, String path) {
        User user = userService.lambdaQuery().eq(User::getAccessKey, accessKey).one();
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的accessKey");
        }
        InterfaceInfo interfaceInfo = interfaceInfoService.lambdaQuery().eq(InterfaceInfo::getUrl, path).eq(InterfaceInfo::getStatus, 1).one();
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的路径");
        }
        return interfaceInvokeInfoService.increaseInvokeCount(user.getId(), interfaceInfo.getId());
    }
}
