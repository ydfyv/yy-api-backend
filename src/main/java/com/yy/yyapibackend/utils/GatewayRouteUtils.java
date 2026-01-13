package com.yy.yyapibackend.utils;

import com.yy.yyapiinterface.api.DynamicRouteService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

/**
 * @author 阿狸
 * @date 2026-01-13
 */
@Component
public class GatewayRouteUtils {

    @DubboReference
    private DynamicRouteService dynamicRouteService;

    public void addRoute(String path) {
        dynamicRouteService.addRoute(path);
    }

    public void deleteRoute(String routeId) {
        dynamicRouteService.deleteRoute(routeId);
    }

    public void updateRoute(String routeId, String uri) {
        dynamicRouteService.updateRoute(routeId, uri);
    }
}
