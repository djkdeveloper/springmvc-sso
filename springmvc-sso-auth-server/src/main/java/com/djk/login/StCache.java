package com.djk.login;

import com.google.common.collect.MapMaker;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dujinkai on 2018/5/29.
 * service ticket 缓存
 */
public class StCache {

    /**
     * 创建一个2分钟有效的tgt缓存   这边可以使用redis 替代
     */
    private Map<String, String> stCache = new MapMaker().expireAfterWrite(2, TimeUnit.MINUTES).makeMap();

    private static StCache ourInstance = new StCache();

    public static StCache getInstance() {
        return ourInstance;
    }

    private StCache() {
    }

    /**
     * 放入st缓存
     *
     * @param token    tokent
     * @param userName 用户名称（简化放入用户名称 可以放入一个用户对象）
     */
    public void addSt(String token, String userName) {
        stCache.put(token, userName);
    }

    /**
     * 移除st缓存
     *
     * @param token token凭证
     */
    public void removeSt(String token) {
        stCache.remove(token);
    }

    /**
     * 根据tokent获得用户名
     *
     * @param token token凭证
     * @return 返回用户名称（可以返回一个用户对象）
     */
    public String getBySt(String token) {
        return stCache.get(token);
    }
}
