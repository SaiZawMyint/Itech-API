package com.itech.api.config;

import com.itech.api.common.enums.UserRoles;
import com.itech.api.persistence.entity.Role;
import com.itech.api.persistence.entity.User;
import com.itech.api.respositories.RoleRepository;
import com.itech.api.respositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeploymentListener {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void listen(){
        addRole();
        addAdmin();
    }
    private void addRole(){
        List<Role> roles = roleRepository.findAll();
        if(roles.isEmpty()){
            // add admin role
            Role adminRole = new Role();
            adminRole.setId(UserRoles.ADMIN.getId());
            adminRole.setName(UserRoles.ADMIN.getDesc());
            roleRepository.save(adminRole);
            // add user role
            Role userRole = new Role();
            userRole.setId(UserRoles.USER.getId());
            userRole.setName(UserRoles.USER.getDesc());
            roleRepository.save(userRole);
        }
    }
    private void addAdmin(){
        List<User> user = userRepository.findAll();
        if(user.isEmpty()){
            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setUsername("System Admin");
            admin.setPassword(passwordEncoder.encode("111111"));
            admin.setEmailVerified(true);
            admin.setRole(roleRepository.getReferenceById(UserRoles.ADMIN.getId()));
            userRepository.save(admin);
        }
    }

}
