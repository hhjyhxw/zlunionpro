package com.icloud.front.tianhaijifen;

import com.icloud.basecommon.service.redis.RedisService;
import com.icloud.basecommon.service.redislock.DistributedLock;
import com.icloud.basecommon.service.redislock.DistributedLockUtil;
import com.icloud.front.tianhaijifen.util.TaihaiScoreTokenUtil;
import com.icloud.modules.wx.entity.WxUser;
import com.icloud.modules.wx.service.WxUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping(value = "${frontPath}/score")
public class TaihaiScoreController {

    public final static Logger log = LoggerFactory.getLogger(TaihaiScoreController.class);
    @Autowired
    private RedisService redisService;
    @Autowired
    private TaihaiScoreTokenUtil taihaiScoreTokenUtil;
    @Autowired
    private DistributedLockUtil distributedLockUtil;
    @Autowired
    private WxUserService wxUserService;


    @RequestMapping("/scoreLogin")
    public String scoreLogin(HttpServletRequest request, HttpSession session, HttpServletResponse response, String businessType) throws IOException {
        try {
            log.info("param:businessType==="+businessType);
           //1、获取token
             Object scoreToken = redisService.get("scoreToken");
             String token = null;
             if(scoreToken!=null){
                 token = taihaiScoreTokenUtil.checkToken(scoreToken.toString());
             }
             if(token!=null){
                 log.info("reids中获取的token="+token);
                 WxUser wxUser = (WxUser) session.getAttribute("wx_user");
                 String restult = taihaiScoreTokenUtil.loginByToken(token,wxUser.getOpenid(),null,businessType);
                 response.sendRedirect(restult);
                 return null;
             }else {
                 log.info("reids中获取不到token或者token失效");
                 DistributedLock lock = distributedLockUtil.getDistributedLock("scoreToken_lockey");
                 try {
                     if (lock.acquire()) {
                         //获取锁成功业务代码
                         token =taihaiScoreTokenUtil.getToken();
                     } else { // 获取锁失败
                         //获取锁失败业务代码
                         response.setCharacterEncoding("UTF-8");
                         response.getWriter().write("系统繁忙，请稍后再试");
                         return null;
                     }
                 } finally {
                     if (lock != null) {
                         lock.release();
                     }
                 }
             }
            WxUser wxUser = (WxUser) session.getAttribute("wx_user");
            String restult = taihaiScoreTokenUtil.loginByToken(token,wxUser.getOpenid(),null,businessType);
            response.sendRedirect(restult);
            return null;
//            //设置服务器端的编码
//            response.setCharacterEncoding("UTF-8");// 默认是ISO-8859-1；该方法必须在response.getWriter()之前进行设置
//            //通知浏览器服务器发送的数据格式
//            response.setHeader("contentType", "text/html; charset=utf-8");
//            //通知浏览器服务器发送的数据格式
//            response.setContentType("text/html; charset=UTF-8");
//            response.getWriter().write(restult);
        }catch (Exception e){
            e.printStackTrace();
//            request.setAttribute("error_msg","系统繁忙，请稍后再试！");
        }
        response.setCharacterEncoding("UTF-8");
        response.setHeader("contentType", "text/html; charset=utf-8");
        response.getWriter().write("系统繁忙，请稍后再试！");
        return null;
    }

}
