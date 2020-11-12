package com.icloud.common.util.wx;


public class WxConst {

	 
		public final static  String CHARSET = "UTF-8";

	
     public static String getwxacodeunlimit = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=ACCESS_TOKEN";
     
//     //支付成功模板id
//     public static String PAY_SUCCESS_TEMPLATE_ID=.get("pay_success_template_id");
//     //砍价成功模板id
//     public static String SEND_ACTIVITY_TEMPLATE_ID=.get("send_activity_template_id");
//    //支付成功模板跳转url
//     public static String PAY_SUCCESS_GO_URL =.get("pay_success_go_url");
//     //砍价成功url
//     public static String ACTIVITY_URL =.get("activity_url");
//     
//     //创建团成功模板消息id
//     public static String CREATE_GROUP_SUCCESS_TEMPLATE_ID=.get("create_group_success_template_id");
//     //参团成功模板id
//     public static String ATTEND_GROUP_SUCCESS_TEMPLATE_ID=.get("attend_group_success_template_id");
//     //拼团成功结果通知模板id
//     public static String GROUP_RESULT_SUCCESS_TEMPLATE_ID=.get("group_result_success_template_id");
//     //拼团 失败结果通知模板id
//     public static String GROUP_RESULT_FAIL_TEMPLATE_ID=.get("group_result_fail_template_id");
//     //团主免单团拼团成功，退款给团主
//     public static String GROUP_RESULT_SUCCESS_MAIN_FREE_TEMPLATE_ID=.get("group_result_success_main_free_template_id");
//     
//     //创建团成功模板跳转url
//     public static String CREATE_GROUP_SUCCESS_URL=.get("create_group_success_url");
//     //参团成功跳转连接url
//     public static String ATTEND_GROUP_SUCCESS_URL=.get("attend_group_success_url");
//     //拼团成功结果通知跳转连接url
//     public static String GROUP_RESULT_SUCCESS_URL=.get("group_result_success_url");
//     //拼团 失败结果通知跳转连接url
//     public static String GROUP_RESULT_FAIL_URL=.get("group_result_fail_url");
//   //拼团 失败结果通知跳转连接url
//     public static String GROUP_RESULT_SUCCESS_MAIN_FREE_URL=.get("group_result_success_main_free_url");
     
 	/**
 	 * 发送模板消息
 	 * */
 	public final static String   SEND_TEMPLATE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    
   
	/**
	 * OAuth2.0引导关注者打开 用户同意授权，获取code页面url.<br/>
	 * 1.scope的设置为：snsapi_base（不弹出授权页面，直接跳转，只能获取用户openid）snsapi_userinfo（弹出授权页面)<br/>
	 * 2.redirect_uri：授权后重定向的回调链接地址，请使用urlencode对链接进行处理<br/>
	 * 方法再commonutil的urlEncodeUTF8()<br/>
	 */
	public final static String FANS_GET_CODE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
	/**
	 * OAuth2.0通过code换取网页授权access_token
	 */
	public final static String OAUTH2_ACCESSTOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	/**
	 * OAuth2.0刷新access_token
	 */
	public final static String REFRESH_ACCESSTOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
	/**
	 * OAuth2.0拉取用户信息(需scope为 snsapi_userinfo)
	 */
	public final static String OAUTH2_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";


	/**
	 * 获取用户基本信息(access_token=网页access_token)
	 */
	public final static String USER_INFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	/**
	 * 获取用户基本信息(access_token=基础access_token)
	 */
	public final static String USER_INFO_BASE = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
	/**
	 * 获取预支付订单
	 */
	public final static String CREATE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	
	/**
	 * 查询订单支付情况
	 * */
	public final static String ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	
	/**
	 * 关闭交易订单
	 * */
	public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
	
	/**
	 * 退款
	 * */
	public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	
	/**
	 * 查询退款
	 * *
	 */
	public final static String REFUND_URL_QUERY = "https://api.mch.weixin.qq.com/pay/refundquery";
	
	public final static String COUPON_URP = "https://api.mch.weixin.qq.com/mmpaymkttransfers/send_coupon";
}
