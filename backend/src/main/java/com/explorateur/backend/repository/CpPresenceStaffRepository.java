package com.explorateur.backend.repository;

import com.explorateur.backend.entity.CpPresenceStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpPresenceStaffRepository extends JpaRepository<CpPresenceStaff, Long> {
    
    List<CpPresenceStaff> findByClasseProgressiveId(Long classeProgressiveId);
    
    void deleteByClasseProgressiveId(Long classeProgressiveId);
    
    boolean existsByClasseProgressiveIdAndStaffId(Long classeProgressiveId, Long staffId);
}
