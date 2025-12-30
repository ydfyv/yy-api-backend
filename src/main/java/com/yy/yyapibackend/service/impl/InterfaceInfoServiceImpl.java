package com.yy.yyapibackend.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yy.yyapibackend.common.ErrorCode;
import com.yy.yyapibackend.constant.CommonConstant;
import com.yy.yyapibackend.exception.ThrowUtils;
import com.yy.yyapibackend.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.yy.yyapibackend.model.entity.*;
import com.yy.yyapibackend.model.enums.HttpMethodEnum;
import com.yy.yyapibackend.model.vo.InterfaceInfoVO;
import com.yy.yyapibackend.model.vo.UserVO;
import com.yy.yyapibackend.service.InterfaceInfoService;
import com.yy.yyapibackend.mapper.InterfaceInfoMapper;
import com.yy.yyapibackend.service.UserService;
import com.yy.yyapibackend.utils.SqlUtils;
import com.yy.yyapiclientsdk.client.YyApiClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 阿狸
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService{

    @Resource
    private UserService userService;

    @Resource
    private YyApiClient yyApiClient;

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo) {
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        String method = interfaceInfo.getMethod();

        ThrowUtils.throwIf(StringUtils.isBlank(url), ErrorCode.PARAMS_ERROR, "url路径参数为空");
        ThrowUtils.throwIf(StringUtils.isBlank(String.valueOf(method)), ErrorCode.PARAMS_ERROR, "请求方式参数为空");
        ThrowUtils.throwIf(StringUtils.isBlank(requestHeader), ErrorCode.PARAMS_ERROR, "请求头参数为空");
        ThrowUtils.throwIf(StringUtils.isBlank(responseHeader), ErrorCode.PARAMS_ERROR, "响应头参数为空");

        String regex = "^https?://[\\w\\-.]+(:\\d+)?(/[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]*)?$";

//        ThrowUtils.throwIf(!regex.matches(url), ErrorCode.PARAMS_ERROR, "url格式不正确");

        ThrowUtils.throwIf(HttpMethodEnum.getEnumByValue(method) == null, ErrorCode.PARAMS_ERROR, "method错误！");
    }
    

    /**
     * 获取查询包装类
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (interfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        Long id = interfaceInfoQueryRequest.getId();
        String name = interfaceInfoQueryRequest.getName();
        String description = interfaceInfoQueryRequest.getDescription();
        String url = interfaceInfoQueryRequest.getUrl();
        String status = interfaceInfoQueryRequest.getStatus();
        String method = interfaceInfoQueryRequest.getMethod();
        Long userId = interfaceInfoQueryRequest.getUserId();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        
        // 拼接查询条件
        queryWrapper.like(ObjectUtils.isNotEmpty(name), "name", name);
        queryWrapper.like(ObjectUtils.isNotEmpty(description), "description", description);
        queryWrapper.like(ObjectUtils.isNotEmpty(url), "url", url);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(method), "method", method);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo, HttpServletRequest request) {
        InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(interfaceInfo);

        Long userId = interfaceInfo.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        interfaceInfoVO.setUser(userVO);

        return interfaceInfoVO;
    }

    @Override
    public Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request) {
        List<InterfaceInfo> interfaceInfoList = interfaceInfoPage.getRecords();
        Page<InterfaceInfoVO> interfaceInfoVOPage = new Page<>(interfaceInfoPage.getCurrent(), interfaceInfoPage.getSize(), interfaceInfoPage.getTotal());
        if (CollUtil.isEmpty(interfaceInfoList)) {
            return interfaceInfoVOPage;
        }

        Set<Long> userIdSet = interfaceInfoList.stream().map(InterfaceInfo::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        List<InterfaceInfoVO> interfaceInfoVOList = interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = InterfaceInfoVO.objToVo(interfaceInfo);
            Long userId = interfaceInfo.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            interfaceInfoVO.setUser(userService.getUserVO(user));
            return interfaceInfoVO;
        }).collect(Collectors.toList());
        interfaceInfoVOPage.setRecords(interfaceInfoVOList);
        return interfaceInfoVOPage;
    }

    @Override
    public void validateAccessible(InterfaceInfo interfaceInfo) {
        // TODO: 根据数据库中的url、method、requestHeader、responseHeader等信息，进行访问校验
        String url = interfaceInfo.getUrl();
        String requestHeader = interfaceInfo.getRequestHeader();
        String responseHeader = interfaceInfo.getResponseHeader();
        String status = interfaceInfo.getStatus();
        String method = interfaceInfo.getMethod();

        com.yy.yyapiclientsdk.model.User user = new com.yy.yyapiclientsdk.model.User();
        user.setName("yy");

        String response = yyApiClient.getNameByPost(user);

        System.out.println("response: " + response);

    }
}




