package com.djk.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dujinkai on 2018/5/30.
 * 首页控制器
 */
@Controller
public class IndexController {

    /**
     * 访问首页
     */
    @RequestMapping("index")
    public ModelAndView index() {
        return new ModelAndView("index");
    }


    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @RequestMapping("/logout")
    public ModelAndView logout() {
        return new ModelAndView("redirect:http://localhost:8080/sso-server/logout");
    }

    @RequestMapping("/logoutinner")
    public int logoutinner(HttpServletRequest request) {
        request.getSession().invalidate();
        return 1;
    }
}
