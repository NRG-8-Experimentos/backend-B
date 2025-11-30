package nrg.inc.synhubbackend.Notification.infrastructure.persistenence.jpa.repositories;

import nrg.inc.synhubbackend.Notification.domain.model.aggregates.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
