package nrg.inc.synhubbackend.Notification.application.internal.queryservices;

import nrg.inc.synhubbackend.Notification.domain.model.aggregates.Notification;
import nrg.inc.synhubbackend.Notification.domain.model.queries.GetAllNotificationsByMemberIdQuery;
import nrg.inc.synhubbackend.Notification.domain.model.services.NotificationQueryService;
import nrg.inc.synhubbackend.Notification.infrastructure.persistenence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> handle(GetAllNotificationsByMemberIdQuery query) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(query.memberId());
    }
}