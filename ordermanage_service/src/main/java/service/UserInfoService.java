package service;

import dao.UserInfoDao;
import domain.Role;
import domain.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserInfoService implements UserDetailsService {
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * 这个方法是给spring security调用的
         * spring security调用该方法，返回一个User对象
         * spring security通过操作该User对象完成认证。
         * 也就是说，不管我们查出来是什么对象，都要转成spring security的User对象
         */
        UserInfo userInfo = userInfoDao.findByUsername(username);
        // 把自己的UserInfo对象转成SpringSecurity的User对象
        List<SimpleGrantedAuthority> list = new ArrayList();
        for (Role role:userInfo.getRoles()) {
            list.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        User user = new User(userInfo.getUsername(),
//                "{noop}" + userInfo.getPassword(),
                userInfo.getPassword(),
                userInfo.getStatus() == 1 ? true : false,
                true,
                true,
                true,
                list);
        return user;
    }
    public List<UserInfo> findAll(){
        return userInfoDao.findAll();
    }

    public void add(UserInfo userInfo){
        userInfo.setId(UUID.randomUUID().toString());
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userInfoDao.add(userInfo);
    }

    public UserInfo findById(String id){
        return userInfoDao.findById(id);
    }

    public void addRoleToUser(String userId, String roleId){
        userInfoDao.addRoleToUser(userId,roleId);
    }
}
