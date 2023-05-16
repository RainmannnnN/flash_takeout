package com.yhy.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhy.takeout.common.Result;
import com.yhy.takeout.entity.User;
import com.yhy.takeout.service.UserService;
import com.yhy.takeout.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        // 获取手机号
        String phone = user.getPhone();
        if (!StringUtils.isEmpty(phone)) {
            // 生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code = " + code);
            // 调用阿里云的短信服务api完成发送短信
//            SMSUtils.sendMessage("falshTakeout", "LTAI5tB9QumBFiu2yH1wUiif", phone, code);
            // 需要将生成的验证码保存到session
            session.setAttribute(phone, code);

            return Result.success("手机验证码发送成功！");
        }

        return Result.error("手机验证码发送失败！");
    }

    /**
     * 用户移动端登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session){
        // 获取手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        // 获取验证码看是否相同，比对验证码
        Object codeInSession = session.getAttribute(phone);

        if(codeInSession != null && codeInSession.equals(code)){
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if (user == null) {
                // 判断手机号是否为新用户，如果是新用户则自动注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return Result.success(user);
        }
        return Result.error("登录失败！");
    }

    /**
     * 用户退出
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public Result<String> logout(HttpSession session){
        log.info("用户退出成功");
        session.removeAttribute("user");
        return Result.success("退出成功!");
    }

}
