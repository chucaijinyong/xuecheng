package com.xuecheng.auth.service;

import com.xuecheng.auth.client.UserClient;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserClient userClient;

    @Autowired
    ClientDetailsService clientDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if(authentication==null){
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(username);
            if(clientDetails!=null){
                //密码
                String clientSecret = clientDetails.getClientSecret();
                return new User(username,clientSecret,AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        //远程调用用户中心根据账号查询用户信息
        XcUserExt userext = userClient.getUserext(username);
        if(userext == null){
            //返回空给spring security表示用户不存在
            return null;
        }
        //取出正确密码（hash值）
        String password = userext.getPassword();
        //从数据库获取权限
        List<XcMenu> permissions = userext.getPermissions();
        if(permissions == null){
            permissions = new ArrayList<>();
        }
        List<String> user_permission = new ArrayList<>();
        permissions.forEach(item-> user_permission.add(item.getCode()));
        String user_permission_string  = StringUtils.join(user_permission.toArray(), ",");
        UserJwt userJwt = new UserJwt(username,
                password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(user_permission_string));
        userJwt.setId(userext.getId());
        userJwt.setUtype(userext.getUtype());//用户类型
        userJwt.setCompanyId(userext.getCompanyId());//所属企业
        userJwt.setName(userext.getName());//用户名称
        userJwt.setUserpic(userext.getUserpic());//用户头像

        // 此时返回的对象包含用户的基本信息和权限，当用户登陆后返回的access_token中就含有了权限信息，可以通过测试类解析token验证
        return userJwt;
    }
}
