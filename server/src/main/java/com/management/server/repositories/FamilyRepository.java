package com.management.server.repositories;

import com.management.server.models.Family;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface FamilyRepository extends JpaRepository<Family,String> {
}
