package nrg.inc.synhubbackend.Notification.domain.model.services;

import nrg.inc.synhubbackend.Notification.domain.model.aggregates.Notification;
import nrg.inc.synhubbackend.Notification.domain.model.queries.GetAllNotificationsByMemberIdQuery;

import java.util.List;

public interface NotificationQueryService {
    List<Notification> handle(GetAllNotificationsByMemberIdQuery query);
}
