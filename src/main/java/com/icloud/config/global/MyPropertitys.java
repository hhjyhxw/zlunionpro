package com.icloud.config.global;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@ConfigurationProperties(prefix="mypros")//加载自定义属性
@Configuration
public class MyPropertitys {

    //项目路径
    private String service_domain;
    //文件上传目录前缀
    private String uploadpath;
    //判断是在本地调试还是发布服务器
    private String activein;
    //
    private Session session;
    //第三方用户授权接口签名keys
    private String userauthorkey;
    ///获取用户是否关注信息 签名key
    private String isfollowkey;
    //微信公众号参数配置
    private Wx wx;
    //积分平台部分接口相关参数
    private ScorePlatform scorePlatform;



    /**
     * spring redis session 相关参数
     */

    @Data
    public static class Session{
        private int timeout;//#spring-session中session过期时间 单位：秒
        private String namespace;//#spring-session中redis命名空间
        private String parentDomainName;// #父域名
        private String cookieName;//#cookie名字

    }

    /**
     * 微信相关参数
     */
    @Data
    public static class Wx {
        private String appid;
        private String appsecret;
        private String getUserInfo;//第三方登陆接口
        private String infokey;//登陆接口签名Key
        private String jssdk_key;//获取jssdk对象key

        private String hostnumber;//gh号 用于区分不同公众号的id
        private String imcchost;//登陆接口签名Key
        private String imcchostport;//登陆接口签名Key
        private String hosttel;//登陆接口签名Key
        private String host;
    }


    /**
     * 微信相关参数
     */
    @Data
    public static class ScorePlatform {
        private Map<String,String> scoreUrlMap;
        private String aes_key;
        private String signkey;
        private String expirationTime;//回调认证缓存信息
        private String scorsLoginUrl;// 从服务号跳转积分平台的链接
        private SingleLogin singleLogin;// 积分平台单点登录参数
        private ActivitySingleLogin activitySingleLogin;//积分平台活动专用单点登录参数
    }

    @Data
    public static class SingleLogin{
        private String score_sid;//
        private String score_aes;//
        private String score_md5;//
        private String score_business_type;//
        private String score_token_url;
        private String score_refresh_token_url;//
        private String score_signle_login_url;
        private String limitedtime;
    }

    @Data
    public static class ActivitySingleLogin{
        private String score_activity_sid;//
        private String score_activity_aes;//
        private String score_activity_md5;//
        private String score_activity_business_type;//
        private String score_activity_token_url;
        private String score_activity_refresh_token_url;//
        private String score_activity_signle_login_url;
        private String activity_limitedtime;
    }
}
