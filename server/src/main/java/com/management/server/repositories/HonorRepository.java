package com.management.server.repositories;

import com.management.server.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HonorRepository extends JpaRepository<Honor, String>{

}
