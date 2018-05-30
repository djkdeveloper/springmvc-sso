package com.djk.login;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dujinkai on 2018/5/30.
 * 子系统退出登录的map
 */
public class LogOutChace {

    /**
     * key是 子系统的sessionid  value 是子系统退出登录的url
     */
    private Map<String, String> map = new HashMap<>();

    private static LogOutChace ourInstance = new LogOutChace();

    public static LogOutChace getInstance() {
        return ourInstance;
    }

    private LogOutChace() {
    }

    /**
     * 获得所有的子系统的退出url
     *
     * @return
     */
    public Map<String, String> getAll() {
        return map;
    }

    /**
     * 新增登出缓存
     *
     * @param sessionId 子系统的sessionId
     * @param logOutUrl 子系统的退出登录url
     */
    public void add(String sessionId, String logOutUrl) {
        map.put(sessionId, logOutUrl);
    }

    /**
     * 移除所有的登出缓存
     */
    public void removeAll() {
        map.clear();
    }

}
