package com.management.server.repositories;

import com.management.server.models.Person;
import com.management.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    User findByUsername(String userName);
}
