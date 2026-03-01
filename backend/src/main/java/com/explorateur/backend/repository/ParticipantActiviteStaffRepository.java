package com.explorateur.backend.repository;

import com.explorateur.backend.entity.ParticipantActiviteStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantActiviteStaffRepository extends JpaRepository<ParticipantActiviteStaff, Long> {
    
    List<ParticipantActiviteStaff> findByActiviteId(Long activiteId);
    
    void deleteByActiviteId(Long activiteId);
}
