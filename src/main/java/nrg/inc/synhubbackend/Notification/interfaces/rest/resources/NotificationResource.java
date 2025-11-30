package nrg.inc.synhubbackend.Notification.interfaces.rest.resources;

public record NotificationResource(
        Long id,
        String message,
        Long relatedTaskId,
        boolean isRead,
        String createdAt
) {}