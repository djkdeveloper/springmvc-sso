package com.djk.index;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dujinkai on 17/5/2.
 * 登入过滤器
 */
public class LoginFilter implements Filter {

    /**
     * 调试日志
     */
    private Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 没有登录
        if (StringUtils.isEmpty(request.getSession().getAttribute("userName"))) {
            try {

                // 判断st是否有效  有效则直接放通 如果st无效 则去认证中心认证
                Map<String, String> result = validateSt(request.getParameter("token"), request.getSession().getId());

                // st 有效 则直接登录成功
                if ("success".equals(result.get("result"))) {
                    request.getSession().setAttribute("userName", result.get("userName"));
                    filterChain.doFilter(request, response);
                    return;
                } else {
                    // st 无效 则跳转至认证中心信息认证
                    String url = "http://localhost:8080/sso-server/tologin?url=http://localhost:8998/sso-client2/index";
                    response.sendRedirect(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("error...", e);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 校验service ticke是否有效
     *
     * @param st 认证中心分发的st
     * @return 返回校验结果
     */
    private Map<String, String> validateSt(String st, String sessionId) {

        Map<String, String> map = new HashMap<>();

        if (StringUtils.isEmpty(st)) {
            return map;
        }


        try {
            URL url = new URL("http://localhost:8080/sso-server/verifyst?st=" + st + "&sessionId=" + sessionId + "&logoutUrl=" + "http://localhost:8998/sso-client2/logoutinner");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
            connection.connect();// 连接会话
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
            connection.disconnect();// 断开连接
            map = (Map<String, String>) JSON.parse(sb.toString());
        } catch (Exception e) {
        }

        return map;

    }

    @Override
    public void destroy() {

    }
}
