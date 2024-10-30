package com.management.server.repositories;

import com.management.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    User findByUsername(String username);
    User findByUserId(Integer userId);

    @Query(value="select count(*) from User where lastLoginTime >?1")
    Integer countLastLoginTime(String date);
    @Query(value = "select userType.id, count(userId) from User group by userType.id" )
    List getCountList();
}
