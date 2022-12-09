package com.itech.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.itech.api.persistence.entity.Role;
import com.itech.api.persistence.entity.User;
import com.itech.api.respositories.RoleRepository;

@SuppressWarnings("unused")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {
//    
//    @Autowired
//    private RoleRepository roleRepository;
//    
//    @Test
//    public void testCreateRole() {
//        Role admin = new Role("ADMIN");
//        Role operator = new Role("OPERATOR");
//        Role premCustomer = new Role("PREMINUM_CUSTOMER");
//        Role customer = new Role("CUSTOMER");
//        
//        roleRepository.saveAll(List.of(admin,operator,premCustomer,customer));
//        
//        long count = roleRepository.count();
//        assertEquals(3, count);
//    }
//    
//    
}
