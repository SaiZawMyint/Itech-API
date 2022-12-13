package com.itech.api.respositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itech.api.persistence.entity.Services;

public interface ServiceRepo extends JpaRepository<Services, Integer>{

    @Query(value="SELECT * FROM Services as s WHERE s.ref_id = ?1",nativeQuery=true)
    Services getByRefId(String refId);

}
