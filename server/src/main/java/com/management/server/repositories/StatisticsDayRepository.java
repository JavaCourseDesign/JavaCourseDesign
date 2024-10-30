package com.management.server.repositories;


import com.management.server.models.StatisticsDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsDayRepository extends JpaRepository<StatisticsDay, Integer> {
    @Query(value=" from StatisticsDay where day >=?1 and day <= ?2 order by day " )
    List<StatisticsDay> findListByDay(String startDay, String endDay);
}
