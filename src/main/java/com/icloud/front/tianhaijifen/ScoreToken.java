package com.icloud.front.tianhaijifen;

import com.icloud.basecommon.util.codec.AesUtils;
import com.icloud.common.ConfigUtil;
import com.icloud.common.DateUtil;


public class ScoreToken {

    private String token;
    private String tokenExpire;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenExpire() {
        return tokenExpire;
    }

    public void setTokenExpire(String tokenExpire) {
        this.tokenExpire = tokenExpire;
    }

    public boolean isValid(String score_aes){
        Long currentTime = System.currentTimeMillis()/1000;//秒
        Long tokenExpire  = DateUtil.getDateWithAll(AesUtils.decode(this.tokenExpire, score_aes)).getTime()/1000;
        if(currentTime-tokenExpire<300){//大于5分中
            return true;
        }
        return false;
    }
}
