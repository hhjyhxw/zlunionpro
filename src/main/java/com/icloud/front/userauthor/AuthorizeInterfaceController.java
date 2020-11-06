package com.icloud.front.userauthor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.Base64;
import com.icloud.basecommon.service.redis.RedisService;
import com.icloud.common.MD5Utils;
import com.icloud.common.util.StringUtil;
import com.icloud.config.global.MyPropertitys;
import com.icloud.front.tianhaijifen.util.AESUtil;
import com.icloud.modules.wx.entity.WxUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @filename      : AuthorizeInterfaceController.java
 * @description   : 第三方调用接口，用户授权获取 用户信息(旧版备份)
 * @author        : zdh
 * @copyright     : zhumeng.com@crowdweb
 *
 * Modification History:
 * Date             Author       Version
 * --------------------------------------
 */
@Slf4j
@Controller
@RequestMapping(value = "/authorizeInterface")//旧版用户接口
public class AuthorizeInterfaceController {
	
	 @Autowired
	 private MyPropertitys myPropertitys;
	@Autowired
	private RedisService redisService;

	private String redirectUrl;

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	 
	/**
     * http://localhost:9098/authorizeInterface/authorizeInterface!authorize.action
	 * @param state
     * @param request
     * @param response
     * @return
     */
	@RequestMapping(value = "/authorizeInterface!authorize.action")
	public String authorize(String state, HttpServletRequest request,
							   HttpServletResponse response){
		log.warn("authorize--->参数state:" + state);
		String thirdRredirectUrl = null;
		try{
			//1、重定向url为空
			if (state==null || "".equals(state)) {
				log.warn("重定向url为空！");
				request.setAttribute("message", "重定向url为空！");
				return "modules/front/error";
			}
			WxUser user = (WxUser)request.getSession().getAttribute("wx_user");
			//2、获取网页授权accessToken和openid
			state = state.replace("-", "=").replace("_", "/");
			thirdRredirectUrl = new String(Base64.decodeFast(state));
			log.warn("getUserInfos decode state:" + thirdRredirectUrl);

			String sign = "openid=" + user.getOpenid() + "&unionid=" + user.getUnionid() + "&key=" + myPropertitys.getUserauthorkey();
			String param = MD5Utils.encode2hex(sign);
			String url = "openid=" + URLEncoder.encode(user.getOpenid(),"utf-8")
					+ "&unionid=" + URLEncoder.encode(user.getUnionid(),"utf-8")
					+ "&sign=" + param+"&nickName="+ URLEncoder.encode(user.getNickname(),"utf-8")
					+"&headPhoto="+ URLEncoder.encode(user.getHeadimgurl(),"utf-8")
					+"&subscribe="+user.getSubscribe();

			if (thirdRredirectUrl.indexOf("?") > 0)
				thirdRredirectUrl = (thirdRredirectUrl + "&" + url);
			else {
				thirdRredirectUrl = (thirdRredirectUrl + "?" + url);
			}
			log.warn("authorize_微信获取openid和unionid成功！" + thirdRredirectUrl);
			response.sendRedirect(thirdRredirectUrl);
			return null;
		}catch (Exception e){
			e.printStackTrace();
			request.setAttribute("message", "授权异常"+e.getMessage());
		}
		return "modules/front/error";
	}


	@RequestMapping("/authorizeInterface!scanAuthorize.action")
	public String scanAuthorize(String code, String state, HttpServletRequest request, HttpSession session, HttpServletResponse response) throws IOException {
		log.warn("sanCanAuthorize-->获取参数 code:" + code + ",state:" + state);

		//2、参数校验 state
		if (!StringUtil.checkObj(state)) {
			log.warn("sanCanAuthorize state为空！");
			request.setAttribute("message","state值为空");
			return "modules/front/error";
		}
		if ("STATE".equals(state)) {
			log.warn("sanCanAuthorize state参数错误！=="+state);
			request.setAttribute("message","state参数错误=="+state);
			return "modules/front/error";
		}
		//3、获取state，并解析
		this.redirectUrl = initCurrencyPageUrl(state);
		try {
			log.warn("state解析后url===" + redirectUrl);
      /*
          state分两种情况
        1、是url，拼上参数，直接返回
        2、非url, 如  redircteChannel=UCSCAN 或者 redircteChannel=UCSCAN?a=b&b=c 或者等等等
           非Url方式 需要根据 UCSCAN 等值动态获取配置url 然后拼上值
       */
			//4、对state 进行判断
			String qrcode = null;//二维码串;
			if ( this.redirectUrl.indexOf("http") < 0) {
				//state解析后是 非URL,则根据配置获取 重定向url
				String[]  arry1 =  this.redirectUrl.split("\\?");//非URL除固定参数外是否有其他参数
				log.warn("arry1==="+arry1);
				if(arry1==null || arry1.length<=0){
					log.warn("state参数错误==="+ this.redirectUrl);
					request.setAttribute("message","state参数错误=="+ this.redirectUrl);
					return "modules/front/error";
				}
				String[]  arry2 = arry1[0].split("=");//对固定参数（用于获取跳转URL的key） 进行处理
				log.warn("arry2==="+arry2);
				if(arry2==null || arry2.length<=0){
					request.setAttribute("message","state参数错误=="+ this.redirectUrl);
					return "modules/front/error";
				}
				this.redirectUrl = myPropertitys.getScorePlatform().getScoreUrlMap().get(arry2[1]);
//				this.redirectUrl = .get(arry2[1]);//根据key获取跳转URL

				log.warn(".get(arry2[1])==="+ this.redirectUrl);
				if (!StringUtil.checkObj( this.redirectUrl)) {
					log.warn("state参数错误,根据key=="+arry2[1]+",获取不到重定向Url");
					request.setAttribute("message","state参数错误,根据key=="+arry2[1]+",获取不到重定向Url");
					return "modules/front/error";
				}
				if(arry1.length>1 && StringUtil.checkObj(arry1[1])){//判断是否包含 除固定参数外的其他参数
					this.redirectUrl =  this.redirectUrl+"?"+arry1[1];
					String[]  arry3 = arry1[1].split("=");
					qrcode = arry3[1];//获取二维码参数
				}
				log.warn("非URL state解析后url===" +  this.redirectUrl);
			}else{
				if ( this.redirectUrl.indexOf("?") > 0) {
					qrcode =  this.redirectUrl.substring( this.redirectUrl.indexOf("?")+1);
				}
			}
			Object baseFormobj = request.getSession().getAttribute("wx_user");
			WxUser wxUser = (WxUser)baseFormobj;
			String openid = wxUser.getOpenid();
			String unionid = wxUser.getUnionid();

			String signkey = myPropertitys.getScorePlatform().getSignkey();
			String aes_key = myPropertitys.getScorePlatform().getAes_key();

//			ImSmSysMemberBind imSmSysBind = imSmSysMemberBindService.findByOpenid(openid);
//			String phone =  imSmSysBind == null? "0000" : StringUtil.checkObj(imSmSysBind.getTelephone())?imSmSysBind.getTelephone():"0000";
			String phone = "0000";

			Long nowTime = System.currentTimeMillis();// 当前时间
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String seqid ="";
			seqid =format.format(nowTime);
			seqid= AESUtil.encrypt(aes_key, openid+String.valueOf(seqid));


			String signString = openid+unionid+phone+ signkey;
			String sign = MD5Utils.encode2hex(signString);
			log.warn("seqid="+seqid+"&qrcode="+qrcode+"&openid="+openid+"&unionid="+unionid+"&phone="+phone+"&sign==="+sign);

			addCurrencyPageUrlParam("openid",AESUtil.encrypt(aes_key, openid), "");
			addCurrencyPageUrlParam("unionid","0000".equals(unionid)?"0000":AESUtil.encrypt(aes_key, unionid), "");
			addCurrencyPageUrlParam("phone","0000".equals(phone)?"0000":AESUtil.encrypt(aes_key, phone), "");
			addCurrencyPageUrlParam("seqid",seqid, "");
			addCurrencyPageUrlParam("timestam",seqid, "");
			addCurrencyPageUrlParam("sign",sign, "");


			//存储redis,用于二次校验
			String cacheString = null;
			cacheString = seqid;
			if(StringUtil.checkObj(qrcode)){
				cacheString+=qrcode;
			}
			cacheString+=openid;
			cacheString+=unionid;
			cacheString+=phone;

			log.warn("key:seqid==="+seqid+"  &value:" + cacheString);

			String expirationTime = myPropertitys.getScorePlatform().getExpirationTime();
			log.error("expirationTime======"+ expirationTime);
			if(expirationTime==null || "".equals(expirationTime)){
				expirationTime="2";
			}
			Long time = new Long(expirationTime);
			log.error("time==="+time);
			redisService.set(seqid,cacheString,time);
			log.warn("返回结果url===" + this.redirectUrl);
			response.sendRedirect( this.redirectUrl);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("授权获取出错！", e);
			request.setAttribute("message","授权获取出错");
		}
		return "modules/front/error";
	}




	/**
	 * 回调校验接口
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/authorizeInterface!reCheckScanAuthorize.action")
	public Map reCheckScanAuthorize(String seqid,String qrcode,String openid,
			String unionid,String phone,String sign) {

		log.warn("参数seqid==="+seqid+"; qrcode=="+qrcode+"; openid==="+openid);
		log.warn("参数unionid==="+unionid+"; phone=="+phone+"; sign==="+sign);
		Map<String,String> jsonMap = new HashMap<String,String>();
		try {
			if(!StringUtil.checkObj(seqid) || !StringUtil.checkObj(qrcode)
					|| !StringUtil.checkObj(openid) || !StringUtil.checkObj(unionid)
					|| !StringUtil.checkObj(phone) || !StringUtil.checkObj(sign)){
				jsonMap.put("status", "error");
				jsonMap.put("errormsg", "参数错误");
				jsonMap.put("seqid", seqid);
				jsonMap.put("qrcode",qrcode);
				jsonMap.put("openid", openid);
				jsonMap.put("unionid",unionid);
				jsonMap.put("phone", phone);
				jsonMap.put("sign", sign);
				log.warn("返回值"+ JSON.toJSONString(jsonMap));
				return jsonMap;
			}
			String checkContent = seqid+qrcode+openid+unionid+phone;
			String signString = checkContent+myPropertitys.getScorePlatform().getSignkey();
			String newSign = MD5Utils.encode2hex(signString);
			log.warn("newsign==="+newSign);
			if(!newSign.equals(sign)){
				jsonMap.put("status", "error");
				jsonMap.put("errormsg", "签名错误");
				jsonMap.put("seqid", seqid);
				jsonMap.put("qrcode",qrcode);
				jsonMap.put("openid", openid);
				jsonMap.put("unionid",unionid);
				jsonMap.put("phone", phone);
				jsonMap.put("sign", sign);
				log.warn("返回值"+ JSON.toJSONString(jsonMap));
				return jsonMap;
			}
			Object checkContentCache = redisService.get(seqid);
			log.warn("参数checkContentCache===" + checkContentCache);
			if(!StringUtil.checkObj(checkContentCache)){
				jsonMap.put("status", "error");
				jsonMap.put("errormsg", "缓存数据已过期");
				jsonMap.put("seqid", seqid);
				jsonMap.put("qrcode",qrcode);
				jsonMap.put("openid", openid);
				jsonMap.put("unionid",unionid);
				jsonMap.put("phone", phone);
				jsonMap.put("sign", sign);
				log.warn("返回值"+ JSON.toJSONString(jsonMap));
				return jsonMap;
			}
			if(checkContent.equals(checkContentCache.toString())){
				jsonMap.put("status", "success");
				jsonMap.put("errormsg", "校验通过");
				jsonMap.put("seqid", seqid);
				jsonMap.put("qrcode",qrcode);
				jsonMap.put("openid", openid);
				jsonMap.put("unionid",unionid);
				jsonMap.put("phone", phone);
				jsonMap.put("sign", sign);
				log.warn("返回值"+ JSON.toJSONString(jsonMap));
				return jsonMap;
			}else{
				jsonMap.put("status", "error");
				jsonMap.put("errormsg", "数据校验失败,与缓存值不一样");
				jsonMap.put("seqid", seqid);
				jsonMap.put("qrcode",qrcode);
				jsonMap.put("openid", openid);
				jsonMap.put("unionid",unionid);
				jsonMap.put("phone", phone);
				jsonMap.put("sign", sign);
				jsonMap.put("sign", sign);
				log.warn("返回值"+ JSON.toJSONString(jsonMap));
				return jsonMap;
			}
		}catch (Exception e){
			e.printStackTrace();
			jsonMap.put("status", "error");
			jsonMap.put("errormsg", "系统错误");
			jsonMap.put("seqid", seqid);
			jsonMap.put("qrcode",qrcode);
			jsonMap.put("openid", openid);
			jsonMap.put("unionid",unionid);
			jsonMap.put("phone", phone);
			jsonMap.put("sign", sign);
			jsonMap.put("sign", sign);
			log.warn("返回值"+ JSON.toJSONString(jsonMap));
			return jsonMap;
		}
	}

	private String initCurrencyPageUrl(String state) {
		if (StringUtil.checkStr(state)) {
			String temp = state.replace("-", "=").replace("_", "/").replace(".", "+");
			return new String(org.apache.shiro.codec.Base64.decode(temp));
		}
		return null;
	}

	private void addCurrencyPageUrlParam(String key, String value, String defaultValue)
	{
		String param = key + "=";
		if ((value == null) || ("".equals(value.trim()))) {
			value = defaultValue;
		}
		param = param + value;
		if (this.redirectUrl.indexOf("?") > 0) {
			this.redirectUrl =  this.redirectUrl + "&" + param;
		} else {
			this.redirectUrl=  this.redirectUrl + "?" + param;
		}
	}


}
