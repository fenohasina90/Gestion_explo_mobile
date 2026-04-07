package com.explorateur.backendbeta.repository;

import com.explorateur.backendbeta.entity.ClasseProgressive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClasseProgressiveRepository extends JpaRepository<ClasseProgressive, Long> {

    @Modifying
    @Query(value = "DELETE FROM cp_details_instructeurs WHERE cp_details_id IN (SELECT id FROM cp_details WHERE classe_progressive_id = :cpId)", nativeQuery = true)
    void deleteCpDetailsInstructeursByCpId(@Param("cpId") Long cpId);

    @Modifying
    @Query(value = "DELETE FROM historique_programmes WHERE classe_progressive_id = :cpId", nativeQuery = true)
    void deleteHistoriqueByCpId(@Param("cpId") Long cpId);

    @Modifying
    @Query(value = "DELETE FROM cp_presence_explo WHERE classe_progressive_id = :cpId", nativeQuery = true)
    void deleteCpPresenceExploByCpId(@Param("cpId") Long cpId);

    @Modifying
    @Query(value = "DELETE FROM cp_presence_staff WHERE classe_progressive_id = :cpId", nativeQuery = true)
    void deleteCpPresenceStaffByCpId(@Param("cpId") Long cpId);

    @Modifying
    @Query(value = "DELETE FROM cp_details WHERE classe_progressive_id = :cpId", nativeQuery = true)
    void deleteCpDetailsByCpId(@Param("cpId") Long cpId);

    @Modifying
    @Query(value = "DELETE FROM classe_progressive WHERE id = :cpId", nativeQuery = true)
    int hardDeleteCpById(@Param("cpId") Long cpId);
}
