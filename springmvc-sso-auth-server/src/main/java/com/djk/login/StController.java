package com.djk.login;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dujinkai on 2018/5/29.
 * st 控制器
 */
@Controller
public class StController {

    /**
     * 验证st是否合法
     *
     * @param st        st
     * @param sessionId 子系统的sessionid
     * @param logoutUrl 子系统退出登录的url
     * @return 返回验证信息
     */
    @RequestMapping("/verifyst")
    @ResponseBody
    public Map<String, Object> verifySt(String st, String sessionId, String logoutUrl) {
        // 没有service ticket 则直接返回错误
        if (StringUtils.isEmpty(st)) {
            return getFailMap();
        } else {
            // 根据st 获得用户信息
            String userName = StCache.getInstance().getBySt(st);
            StCache.getInstance().removeSt(st);

            // 没有用户信息返回失败
            if (StringUtils.isEmpty(userName)) {
                return getFailMap();
            }

            Map<String, Object> map = new HashMap<>();
            map.put("result", "success");
            map.put("userName", userName);
            LogOutChace.getInstance().add(sessionId, logoutUrl);
            return map;
        }
    }

    /**
     * 获得认证失败的map
     *
     * @return 返回认证失败的map
     */
    private Map<String, Object> getFailMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("result", "fail");
        return map;
    }
}
