package nrg.inc.synhubbackend.Notification.interfaces.rest.transform;

import nrg.inc.synhubbackend.Notification.domain.model.aggregates.Notification;
import nrg.inc.synhubbackend.Notification.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getId(),
                entity.getMessage(),
                entity.getRelatedTaskId(),
                entity.isRead(),
                entity.getCreatedAt().toString()
        );
    }
}