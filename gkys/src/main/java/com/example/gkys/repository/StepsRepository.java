package com.example.gkys.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.StepsModel;

@Repository
public interface StepsRepository extends CrudRepository<StepsModel, Long> {
    @Query("SELECT FUNCTION('DAYOFWEEK', s.date) AS dayOfWeek, SUM(s.steps) " +
           "FROM StepsModel s " +
           "WHERE s.user.id = :userId " +
           "AND s.date BETWEEN :startOfWeek AND :endOfWeek " +
           "GROUP BY FUNCTION('DAYOFWEEK', s.date)")
    List<Object[]> findStepsPerDayOfWeek(@Param("userId") Long userId,
                                         @Param("startOfWeek") Date startOfWeek,
                                         @Param("endOfWeek") Date endOfWeek);
}
