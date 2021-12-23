package com.allenyll.sw.common.entity.wechat;

import com.allenyll.sw.common.enums.Gender;
import lombok.Data;

/**
* @Title: WxUserInfo
* @Package com.allenyll.sw.wechat.entity
* @Description: 微信用户
* @author yu.leilei
* @date 2018/10/19 17:45
* @version V1.0
*/
@Data
public class WxUserInfo {

    private Long id;
    private String username;
    private Long phone;
    private Gender gender;
    private String vcode;
    private String password;
    private String promotionCode;
    private String InvitationCode;
    private String clientAssertion;
    private String code;

    public WxUserInfo() {
    }
}
