package com.djk.login;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by dujinkai on 2018/5/29.
 * 登录控制器
 */
@Controller
public class LoginController {


    /**
     * 跳转到登录页面
     *
     * @param url 回调客户端的url地址
     * @return 返回登录页面
     */
    @RequestMapping("/tologin")
    public ModelAndView toLogin(HttpServletRequest request, String url) {

        // 判断cookie中是否有tgc 如果有tgc说明登录成功过 没有说明没有登录过 则跳转至登录页面
        String tgc = getTgcFromCokie(request);
        if (StringUtils.isEmpty(tgc) || StringUtils.isEmpty(TgTCache.getInstance().getTgt(tgc))) {
            // 将客户端的地址放入sesion中方便后面调用
            request.getSession().setAttribute("redirctUrl", url);

            return new ModelAndView("login");
        } else {
            return handleAfterLoginSuccess(url, TgTCache.getInstance().getTgt(tgc));
        }

    }

    /**
     * 用户名密码正确返回成功 0 失败返回-1
     *
     * @param userName 用户名
     * @param password 密码
     * @return 成功返回0 失败返回-1
     */
    @RequestMapping("/login")
    @ResponseBody
    public int login(HttpServletRequest request, HttpServletResponse response, String userName, String password) {
        // 登录结果
        int result = "djk".equals(userName) && "whx".equals(password) ? 0 : -1;

        // 登录成功则生成tgc 放入cookie中
        if (result == 0) {
            // tgc
            String tgc = UUID.randomUUID().toString();
            // 将tgc放入缓存
            TgTCache.getInstance().addTgt(tgc, userName);
            // 生成token  并且放入cookie中
            Cookie cookie = new Cookie("TGC", tgc);
            cookie.setMaxAge(60 * 60 * 2);//生命周期2小时
            response.addCookie(cookie);

            // 将用户名 放入session  方便后面使用
            request.getSession().setAttribute("userName", userName);

        }

        return result;
    }


    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public int logout(HttpServletRequest request) {
        // 销毁tgc
        TgTCache.getInstance().removeTgt(getTgcFromCokie(request));

        Map<String, String> map = LogOutChace.getInstance().getAll();

        map.forEach((key, value) -> subSystemLogOut(value, key));

        LogOutChace.getInstance().removeAll();

        return 1;
    }

    /**
     * 各个子系统退出登录
     */
    private void subSystemLogOut(String logOutUrl, String sessionId) {

        try {
            URL url = new URL(logOutUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            connection.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            connection.setDoInput(true);

            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 登录成功
     *
     * @return 返回登录成功后的页面
     */
    @RequestMapping("/loginsuccess")
    public ModelAndView loginSuccess(HttpServletRequest request) {
        String url = (String) request.getSession().getAttribute("redirctUrl");
        String userName = (String) request.getSession().getAttribute("userName");
        request.getSession().removeAttribute("redirctUrl");
        request.getSession().removeAttribute("userName");
        return handleAfterLoginSuccess(url, userName);
    }

    /**
     * 登录成功处理的
     *
     * @param url      客户端url
     * @param userName 用户名
     * @return 如果客户端url 不为空则重订向到客户端的url  如果为空 则直接返回sso服务器的欢迎页面
     */
    private ModelAndView handleAfterLoginSuccess(String url, String userName) {
        if (StringUtils.isEmpty(url)) {
            return new ModelAndView("index");
        }

        // 重定向到客户端地址
        String st = UUID.randomUUID().toString();

        // 将用户信息 放入缓存中
        StCache.getInstance().addSt(st, userName);

        return new ModelAndView("redirect:" + url + "?token=" + st);
    }


    /**
     * 从cookie中获得tgc
     *
     * @return 返回tgc
     */
    private String getTgcFromCokie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        // 没有cookie直接返回空
        if (ArrayUtils.isEmpty(cookies)) {
            return "";
        }

        // tgc的cookie
        Optional<Cookie> tgcCookie = Arrays.stream(cookies).filter(cookie -> "TGC".equals(cookie.getName())).findFirst();

        // 存在tgc 的cookie
        if (tgcCookie.isPresent()) {
            return tgcCookie.get().getValue();
        }
        return "";
    }
}
