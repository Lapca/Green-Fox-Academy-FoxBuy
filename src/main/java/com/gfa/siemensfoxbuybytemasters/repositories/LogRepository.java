package com.gfa.siemensfoxbuybytemasters.repositories;

import com.gfa.siemensfoxbuybytemasters.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LogRepository extends JpaRepository<Log,Long> {

   @Query("SELECT l FROM Log l WHERE DATE(l.timestamp) >= :date")
    List<Log> findByDateAfter(@Param("date") LocalDate date);

}
