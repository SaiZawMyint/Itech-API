package com.itech.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.itech.api.persistence.entity.Role;
import com.itech.api.persistence.entity.User;
import com.itech.api.respositories.UserRepository;
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
//    @Autowired private UserRepository repo;
//    
//    @Test
//    public void testCreateUser() {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//         
//        User newUser = new User();
//        newUser.setUsername("admin");
//        newUser.setEmail("admin@gmail.com");
//        newUser.setPassword(passwordEncoder.encode("admin@123"));
//        
//        User newUser1 = new User();
//        newUser1.setUsername("operator");
//        newUser1.setEmail("operator@gmail.com");
//        newUser1.setPassword(passwordEncoder.encode("operator@123"));
//        
//        repo.saveAll(List.of(newUser,newUser1));
//         
//        long count = repo.count();
//        assertEquals(2, count);
//    }
    
//    @Test
//    public void testAssignRoleToUser() {
//        Integer userId = 2;
//        Integer roleId = 2;
//        User user = repo.findById(userId).get();
//        user.addRole(new Role(roleId));
//         
//        User updatedUser = repo.save(user);
//        assertThat(updatedUser.getRoles()).hasSize(1);
         
//    }
}
