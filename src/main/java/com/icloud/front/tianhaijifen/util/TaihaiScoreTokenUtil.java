package com.icloud.front.tianhaijifen.util;

import com.alibaba.fastjson.JSONObject;
import com.icloud.basecommon.service.redis.RedisService;
import com.icloud.basecommon.util.http.HttpClientUtils;
import com.icloud.common.DateUtil;
import com.icloud.common.MD5Utils;
import com.icloud.common.util.StringUtil;
import com.icloud.config.global.MyPropertitys;
import com.icloud.front.tianhaijifen.ScoreToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TaihaiScoreTokenUtil {

    public final static Logger log = LoggerFactory.getLogger(TaihaiScoreTokenUtil.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private MyPropertitys myPropertitys;
    /**
     * 获取token
     * @return
     */
    public String getToken() throws Exception {
        MyPropertitys.SingleLogin singleLogin = myPropertitys.getScorePlatform().getSingleLogin();
       String sid = singleLogin.getScore_sid();
       String score_aes = singleLogin.getScore_aes();
       String score_md5 = singleLogin.getScore_md5();
        log.info("keys:score_aes=="+score_aes+"  score_md5=="+score_md5);
        String timeStamp = DateUtil.formatTimestamp(new Date());
//       String timeStamp = AESUtil.encrypt(score_aes,DateUtil.formatTimestamp(new Date()));
       String sign = MD5Utils.encode2hex(sid+timeStamp+score_md5);
       log.info("出参：sid=="+sid+" timeStamp=="+timeStamp+" sign=="+sign);
       String score_token_url = singleLogin.getScore_token_url()+"?sid="+sid+"&timeStamp="+AESUtil.encrypt(score_aes,timeStamp)+"&sign="+sign;
        log.info("score_token_url="+score_token_url);
        String result = HttpClientUtils.get(score_token_url);
        log.info("申请token请求结果result==="+result);
        JSONObject relJson = null;
        if(result.contains("code")){
            relJson = JSONObject.parseObject(result);
            if("0".equals(relJson.getString("code"))){
                ScoreToken token = new ScoreToken();
                token.setToken(relJson.getString("token"));
                token.setTokenExpire(relJson.getString("tokenExpire"));
                log.info("tokenExpire="+AESUtil.decrypt(score_aes,token.getTokenExpire()));
//                redisService.set("scoreToken",JSONObject.toJSONString(token));
                redisService.set("scoreToken",result);
                return relJson.getString("token");
            }
        }
        return null;
    }

    /**
     * 刷新token
     * @return
     */
    public void refreshToken() throws Exception {
        Object obj = redisService.get("scoreToken");
        if(obj==null){
            log.info("旧的stoken不存在");
            return;
        }
        MyPropertitys.SingleLogin singleLogin = myPropertitys.getScorePlatform().getSingleLogin();
        String tokenString = obj.toString();
        JSONObject tokenJson = null;
        if(tokenString.contains("code")){
            tokenJson = JSONObject.parseObject(tokenString);
            log.info("refresh_tokenExpire1=" + AESUtil.decrypt(singleLogin.getScore_aes(), tokenJson.getString("tokenExpire")));
            if("0".equals(tokenJson.getString("code"))) {
                String oldToken = tokenJson.getString("token");
                String tokenExpire = tokenJson.getString("tokenExpire");
                log.info("refresh_tokenExpire=" + AESUtil.decrypt(singleLogin.getScore_aes(), tokenExpire));

                Long currentTime = System.currentTimeMillis() / 1000;//秒
                Long tokenExpireTime = DateUtil.getDateWithAll(AESUtil.decrypt(singleLogin.getScore_aes(),tokenExpire)).getTime() / 1000;
                Long limitedtime = singleLogin.getLimitedtime()!=null?Long.parseLong(singleLogin.getLimitedtime()):600;
                if (currentTime - tokenExpireTime < limitedtime) {//大于5分中
                    log.info("token还在有效期");
                    return;
                }
            }
         }
        /////
        String oldToken = tokenJson.getString("token");
        String sid = singleLogin.getScore_sid();
        String score_aes = singleLogin.getScore_aes();
        String score_md5 = singleLogin.getScore_md5();
        String timeStamp = DateUtil.formatTimestamp(new Date());
        log.info("签名参数：sid=="+sid+" token=="+AESUtil.decrypt(score_aes,oldToken)+" timeStamp=="+timeStamp+"score_md5=="+score_md5);
        String sign = MD5Utils.encode2hex(sid+AESUtil.decrypt(score_aes,oldToken)+timeStamp+score_md5);
        String score_refresh_token_url =singleLogin.getScore_refresh_token_url()+"?sid="+sid+"&token="+oldToken+"&timeStamp="+AESUtil.encrypt(score_aes,timeStamp)+"&sign="+sign;
        log.info("score_refresh_token_url="+score_refresh_token_url);
        String result = HttpClientUtils.get(score_refresh_token_url);
        log.info("刷新token请求结果result==="+result);
        JSONObject relJson = null;
        if(result.contains("code")){
            relJson = JSONObject.parseObject(result);
            if("0".equals(relJson.getString("code"))){
                ScoreToken token = new ScoreToken();
                token.setToken(relJson.getString("token"));
                token.setTokenExpire(relJson.getString("tokenExpire"));
//                redisService.set("scoreToken",JSONObject.toJSONString(token));
                redisService.set("scoreToken",result);
            }
        }
        return;
    }

    /**
     * 根据token登陆积分商城
     * @return
     */
    public String loginByToken(String token, String userAccount,String phone,String businessType1) throws Exception {
        MyPropertitys.SingleLogin singleLogin = myPropertitys.getScorePlatform().getSingleLogin();
        String sid = singleLogin.getScore_sid();
        String score_aes = singleLogin.getScore_aes();
        String score_md5 = singleLogin.getScore_md5();
        String businessType = null;
        if(StringUtil.checkStr(businessType1)){
            businessType = businessType1;
        }else{
            businessType = singleLogin.getScore_business_type();
        }
//        String businessType = .get("score_business_type");
        String timeStamp = DateUtil.formatTimestamp(new Date());
        String accountType = "2";// accountType =2，accountType =微信openId
        if(StringUtil.checkStr(phone)){
            accountType = "4";// accountType =2，accountType =微信openId
            userAccount = userAccount+","+phone;
        }
        log.info("签名参数：sid=="+sid+";accountType==="+accountType+";userAccount=="+userAccount
                +";timeStamp=="+timeStamp+";businessType=="+businessType+";token=="+AESUtil.decrypt(score_aes,token)
                +";score_md5=="+score_md5);
        String sign = MD5Utils.encode2hex(sid+accountType+userAccount+timeStamp+businessType+AESUtil.decrypt(score_aes,token)+score_md5);
        log.info("aes_token==="+token);
        userAccount = AESUtil.encrypt(score_aes,userAccount);
        timeStamp = AESUtil.encrypt(score_aes,timeStamp);
        String score_signle_login_url =singleLogin.getScore_signle_login_url()
                +"?sid="+sid+"&userAccount="+userAccount+"&accountType="+accountType+"&businessType="+businessType+"&timeStamp="+timeStamp+"&sign="+sign;
        log.info("score_signle_login_url==="+score_signle_login_url);
        return score_signle_login_url;
    }

    /**
     *
     * @param tokenString 申请token的返回结果
     * @return
     */
    public String checkToken(String tokenString) throws Exception {
        JSONObject tokenJson = null;
        MyPropertitys.SingleLogin singleLogin = myPropertitys.getScorePlatform().getSingleLogin();
        if(tokenString.contains("code")){
            tokenJson = JSONObject.parseObject(tokenString);
            log.info("refresh_tokenExpire1=" + AESUtil.decrypt(singleLogin.getScore_aes(), tokenJson.getString("tokenExpire")));
            if("0".equals(tokenJson.getString("code"))) {
                String oldToken = tokenJson.getString("token");
                String tokenExpire = tokenJson.getString("tokenExpire");
                log.info("refresh_tokenExpire=" + AESUtil.decrypt(singleLogin.getScore_aes(), tokenExpire));

                Long currentTime = System.currentTimeMillis() / 1000;//秒
                Long tokenExpireTime = DateUtil.getDateWithAll(AESUtil.decrypt(singleLogin.getScore_aes(),tokenExpire)).getTime() / 1000;
                Long limitedtime = singleLogin.getLimitedtime()!=null?Long.parseLong(singleLogin.getLimitedtime()):600;
                if (currentTime - tokenExpireTime < limitedtime) {//大于5分中
                    log.info("token还在有效期");
                    return tokenJson.getString("token");
                }
            }
        }
        return null;
    }

}
