package com.management.server.repository;


import com.management.server.models.Dormitory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface DormitoryRepository extends JpaRepository<Dormitory,Integer> {
    @Query("select d from Dormitory d")
   List<Dormitory> findAll();

    @Modifying
    @Transactional
    @Query("update Dormitory set name=?1 where id=?2")
    void updateDormNameById(String name,Integer id);

}
