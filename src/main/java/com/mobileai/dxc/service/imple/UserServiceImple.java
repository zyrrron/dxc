package com.mobileai.dxc.service.imple;

import java.util.Random;

import com.mobileai.dxc.component.MailSender;
import com.mobileai.dxc.db.mapper.AccountMapper;
import com.mobileai.dxc.db.mapper.UserMapper;
import com.mobileai.dxc.service.UserService;
import com.mobileai.dxc.util.MD5Utils;
import com.mobileai.dxc.util.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImple implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AccountMapper accountMapper;

    private String identifyCode;
    private String email;

    @Override
    public Result identify(String toEmail) {
        email = toEmail;
        identifyCode = getidentifyCode();
        MailSender.sendMail(toEmail, identifyCode);
        return new Result(200);
    }

    @Override
    public Result signup(String identifyCode, String name, String password, boolean seller) {
        if (identifyCode == this.identifyCode) {
            int targetid = userMapper.selectIdByEmail(email);
            String secretpassword = MD5Utils.md5(password);

            accountMapper.addAccount(name, secretpassword, seller, targetid);
            return new Result(200);
        }else{
            return new Result(100);
        }
    }

    @Override
    public Result validate(String name, String password) {
        String secretpassword = MD5Utils.md5(password);

        if (secretpassword == accountMapper.selectPasswordByUserName(name)) {
            return new Result(200);
        } else {
            return new Result(100);
        }
    }

    // 得到随机8位验证码
    public String getidentifyCode() {
        String number[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<8;i++){
            str.append(number[(int)random.nextInt(10)]);
        }
        
        return str.toString();

    }

}