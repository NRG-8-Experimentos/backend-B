package nrg.inc.synhubbackend.Notification.domain.model.commands;

public record CreateNotificationCommand(Long memberId, String message, Long relatedTaskId) {}
