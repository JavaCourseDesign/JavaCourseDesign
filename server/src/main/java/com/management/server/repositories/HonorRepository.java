package com.management.server.repositories;

import com.management.server.models.Honor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface HonorRepository extends JpaRepository<Honor, String>{
    Integer deleteByHonorId(String honorId);
    Honor findByHonorId(String honorId);

}
