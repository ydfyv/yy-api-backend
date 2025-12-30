package com.yy.yyapibackend.controller.api;

import com.google.gson.Gson;
import com.yy.yyapibackend.common.BaseResponse;
import com.yy.yyapibackend.common.ErrorCode;
import com.yy.yyapibackend.common.ResultUtils;
import com.yy.yyapibackend.exception.BusinessException;
import com.yy.yyapiclientsdk.client.YyApiClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 阿狸
 * @date 2025-12-30
 */
@RestController
@RequestMapping("/debug")
public class ApiDebugController {

    @Resource
    private YyApiClient yyApiClient;

    /**
     * 调试接口
     *
     * @param jsonStr
     * @return
     */
    @PostMapping("/debugInterface")
    public BaseResponse<String> debugInterface(@RequestBody String jsonStr) {
        if (jsonStr == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Gson gson = new Gson();
        com.yy.yyapiclientsdk.model.User user = gson.fromJson(jsonStr, com.yy.yyapiclientsdk.model.User.class);
        String result = yyApiClient.getNameByPost(user);

        return ResultUtils.success(result);
    }
}
