package com.stockportfolio.alertservice.repository;

import com.stockportfolio.alertservice.entity.AlertNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertNotificationRepository extends JpaRepository<AlertNotification, Long> {

    List<AlertNotification> findByUserId(Long userId);

    List<AlertNotification> findByAlertId(Long alertId);
}
