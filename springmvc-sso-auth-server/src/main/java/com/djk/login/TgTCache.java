package com.djk.login;

import com.google.common.collect.MapMaker;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dujinkai on 2018/5/29.
 * TGT 缓存
 */
public class TgTCache {

    /**
     * 创建一个2小时有效的tgt缓存   这边可以使用redis 替代
     */
    private Map<String, String> tgtCache = new MapMaker().expireAfterWrite(2, TimeUnit.HOURS).makeMap();

    private static TgTCache ourInstance = new TgTCache();

    public static TgTCache getInstance() {
        return ourInstance;
    }

    private TgTCache() {
    }

    /**
     * 放入 tgt缓存
     *
     * @param tgc      tgckey
     * @param userName 用户名（tgt  可以为一个用户对象 这边演示简单点就放用户名）
     */
    public void addTgt(String tgc, String userName) {
        tgtCache.put(tgc, userName);
    }

    /**
     * 从缓存中获得tgt
     *
     * @param tgc tgc key
     * @return 返回tgt值
     */
    public String getTgt(String tgc) {
        return tgtCache.get(tgc);
    }

    /**
     * 销毁tgc
     *
     * @param tgc tgc凭证
     */
    public void removeTgt(String tgc) {
        tgtCache.remove(tgc);
    }
}
