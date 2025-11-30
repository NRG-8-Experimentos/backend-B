package nrg.inc.synhubbackend.Notification.domain.model.services;

import nrg.inc.synhubbackend.Notification.domain.model.aggregates.Notification;
import nrg.inc.synhubbackend.Notification.domain.model.commands.CreateNotificationCommand;
import nrg.inc.synhubbackend.Notification.domain.model.commands.MarkAsReadCommand;

import java.util.Optional;

public interface NotificationCommandService {
    Optional<Notification> handle(CreateNotificationCommand command);
    void handle(MarkAsReadCommand command);

}
