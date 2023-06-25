package com.touramigo.service.repository;

import com.touramigo.service.entity.history.PartnerHistory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerHistoryRepository extends JpaRepository<PartnerHistory, UUID> {
    List<PartnerHistory> findAllByPartnerIdOrderByDateLogDesc(UUID partnerId);
}
