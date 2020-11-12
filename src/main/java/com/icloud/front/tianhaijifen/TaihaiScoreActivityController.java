//package com.icloud.front.tianhaijifen;
//
//import cn.hutool.core.codec.Base64;
//import com.icloud.basecommon.service.redis.RedisService;
//import com.icloud.basecommon.service.redislock.DistributedLockUtil;
//import com.icloud.common.util.StringUtil;
//import com.icloud.front.tianhaijifen.util.TaihaiScoreActivityTokenUtil;
//import com.icloud.model.crowdfunding.ZlwxCrowdfundingSign;
//import com.icloud.model.imsm.ImSmCustomer;
//import com.icloud.model.imsm.ImSmSysMemberBind;
//import com.icloud.service.crowdfunding.ZlwxCrowdfundingSignService;
//import com.icloud.service.imsm.ImSmSysMemberBindService;
//import com.icloud.service.redis.RedisService;
//import com.icloud.service.redislock.DistributedLock;
//import com.icloud.service.redislock.DistributedLockUtil;
//import com.icloud.web.tianhaijifen.util.TaihaiScoreActivityTokenUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//
///**
// * 特定活动单点登陆
// */
//@Controller
//@RequestMapping(value = "${frontPath}/scoreactivity")
//public class TaihaiScoreActivityController {
//
//    public final static Logger log = LoggerFactory.getLogger(TaihaiScoreActivityController.class);
//    @Autowired
//    private RedisService redisService;
//    @Autowired
//    private TaihaiScoreActivityTokenUtil taihaiScoreActivityTokenUtil;
//
//    @Autowired
//    private DistributedLockUtil distributedLockUtil;
//
//
//    @RequestMapping("/scoreLogin")
//    public String scoreLogin(HttpServletRequest request, HttpSession session, HttpServletResponse response, String businessType, String businessParam, Long activityId) throws IOException {
//        try {
//            //判断用户是否有资格去兑换商城
//            log.info("param:activityId==="+activityId);
//            log.info("param:businessParam==="+businessParam);
//            log.info("param:businessType==="+businessType);
//            if(activityId==null || activityId<=0){
//                request.setAttribute("message","活动参数错误");
//               return "modules/front/error";
//            }
//            ImSmCustomer imSmCustomer = (ImSmCustomer) session.getAttribute("imSmCustomer");
//            ZlwxCrowdfundingSign zlwxCrowdfundingSign = new ZlwxCrowdfundingSign();
//            zlwxCrowdfundingSign.setOpenid(imSmCustomer.getOpenid());
//            zlwxCrowdfundingSign.setCrowdfundingid(activityId);
//            List<ZlwxCrowdfundingSign> list = zlwxCrowdfundingSignService.findList(zlwxCrowdfundingSign);
//            if(list==null || list.size()==0){
//                request.setAttribute("message","您还未获得活动资格");
//                return "modules/front/error";
//            }
//            if(StringUtil.checkStr(businessParam)){
//                businessParam = Base64.decodeStr(businessParam);
//            }
//            log.info("decode:businessParam==="+businessParam);
//           //1、获取token
//             Object scoreToken = redisService.get("scoreActivityToken");
//             String token = null;
//             if(scoreToken!=null){
//                 token = taihaiScoreActivityTokenUtil.checkToken(scoreToken.toString());
//             }
//             if(token!=null){
//                 log.info("reids中获取的activitytoken="+token);
//
//                 ImSmSysMemberBind imSmSysMemberBind = imSmSysMemberBindService.findByOpenid(imSmCustomer.getOpenid());
//                 String restult = taihaiScoreActivityTokenUtil.loginByToken(token,imSmCustomer.getOpenid(),imSmSysMemberBind!=null?imSmSysMemberBind.getTelephone():null,businessType,businessParam);
//                 response.sendRedirect(restult);
//                 return null;
//             }else {
//                 log.info("reids中获取不到activitytoken或者activitytoken失效");
//                 DistributedLock lock = distributedLockUtil.getDistributedLock("scoreToken_lockey");
//                 try {
//                     if (lock.acquire()) {
//                         //获取锁成功业务代码
//                         token =taihaiScoreActivityTokenUtil.getToken();
//                     } else { // 获取锁失败
//                         //获取锁失败业务代码
//                         response.setCharacterEncoding("UTF-8");
//                         response.getWriter().write("系统繁忙，请稍后再试");
//                         return null;
//                     }
//                 } finally {
//                     if (lock != null) {
//                         lock.release();
//                     }
//                 }
//             }
//            ImSmSysMemberBind imSmSysMemberBind = imSmSysMemberBindService.findByOpenid(imSmCustomer.getOpenid());
//            String restult = taihaiScoreActivityTokenUtil.loginByToken(token,imSmCustomer.getOpenid(),imSmSysMemberBind!=null?imSmSysMemberBind.getTelephone():null,businessType,businessParam);
//            response.sendRedirect(restult);
//            return null;
////            //设置服务器端的编码
////            response.setCharacterEncoding("UTF-8");// 默认是ISO-8859-1；该方法必须在response.getWriter()之前进行设置
////            //通知浏览器服务器发送的数据格式
////            response.setHeader("contentType", "text/html; charset=utf-8");
////            //通知浏览器服务器发送的数据格式
////            response.setContentType("text/html; charset=UTF-8");
////            response.getWriter().write(restult);
//        }catch (Exception e){
//            e.printStackTrace();
////            request.setAttribute("error_msg","系统繁忙，请稍后再试！");
//        }
//        response.setCharacterEncoding("UTF-8");
//        response.setHeader("contentType", "text/html; charset=utf-8");
//        response.getWriter().write("系统繁忙，请稍后再试！");
//        return null;
//    }
//
//}
