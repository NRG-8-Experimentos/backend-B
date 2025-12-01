package nrg.inc.synhubbackend.Notification.application.internal.commandservices;

import nrg.inc.synhubbackend.Notification.domain.model.aggregates.Notification;
import nrg.inc.synhubbackend.Notification.domain.model.commands.CreateNotificationCommand;
import nrg.inc.synhubbackend.Notification.domain.model.commands.MarkAsReadCommand;
import nrg.inc.synhubbackend.Notification.domain.model.services.NotificationCommandService;
import nrg.inc.synhubbackend.Notification.infrastructure.persistenence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<Notification> handle(CreateNotificationCommand command) {
        var notification = new Notification(
                command.memberId(),
                command.message(),
                command.relatedTaskId()
        );
        var createdNotification = notificationRepository.save(notification);
        return Optional.of(createdNotification);
    }

    @Override
    public void handle(MarkAsReadCommand command) {
        var notificationOpt = notificationRepository.findById(command.notificationId());
        if (notificationOpt.isEmpty()) {
            throw new IllegalArgumentException("Notification not found");
        }
        var notification = notificationOpt.get();
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}