// src/main/java/com/afci/training/planning/repository/SessionRepository.java
package com.afci.training.planning.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
//import dans ton interface existante :
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.afci.training.planning.repository.SessionListProjection;

import com.afci.training.planning.entity.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer>, JpaSpecificationExecutor<Session> {


    @Query(value = "SELECT * FROM v_session_with_assigned_trainer", nativeQuery = true)
    List<SessionListProjection> listWithAssignedTrainer();
    @Query(value = "SELECT * FROM v_session_with_assigned_trainer WHERE id_session = :id", nativeQuery = true)
    SessionListProjection findOneWithAssignedTrainer(@Param("id") Integer id);
    
}
