package com.explorateur.backend.repository;

import com.explorateur.backend.entity.RolesStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesStaffRepository extends JpaRepository<RolesStaff, Long> {
    Optional<RolesStaff> findByRoleName(String roleName);
}
