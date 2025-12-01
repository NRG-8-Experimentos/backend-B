package nrg.inc.synhubbackend.tasks.domain.services;

import nrg.inc.synhubbackend.requests.domain.model.commands.CreateRequestCommand;
import nrg.inc.synhubbackend.requests.domain.services.RequestCommandService;
import nrg.inc.synhubbackend.tasks.domain.model.aggregates.Task;
import nrg.inc.synhubbackend.tasks.domain.model.valueobjects.TaskStatus;
import nrg.inc.synhubbackend.tasks.infrastructure.persistence.jpa.repositories.TaskRepository;
import nrg.inc.synhubbackend.Notification.domain.model.commands.CreateNotificationCommand;
import nrg.inc.synhubbackend.Notification.domain.model.services.NotificationCommandService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TaskStatusScheduler {
    private final TaskRepository taskRepository;
    private final RequestCommandService requestCommandService;
    private final NotificationCommandService notificationCommandService;

    public TaskStatusScheduler(
            TaskRepository taskRepository,
            RequestCommandService requestCommandService,
            NotificationCommandService notificationCommandService) {
        this.taskRepository = taskRepository;
        this.requestCommandService = requestCommandService;
        this.notificationCommandService = notificationCommandService;
    }

    // ------------------------------------------------------------------
    // SCHEDULER PRINCIPAL (Ejecuta Expired y luego Alerts)
    // ------------------------------------------------------------------
    @Scheduled(fixedRate = 30000) // Ejecuta cada 30 segundos
    @Transactional
    public void runScheduledTasks() {
        updateExpiredTasks();
        sendDueDateAlerts();
    }

    private void updateExpiredTasks() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        taskRepository.findAllByStatusAndDueDateBefore(TaskStatus.IN_PROGRESS, now)
                .forEach(task -> {
                    System.out.println("Updating task with ID: " + task.getId());
                    task.setStatus(TaskStatus.EXPIRED);
                    var createRequestCommand = new CreateRequestCommand(
                            "La tarea venció automáticamente.",
                            "EXPIRED",
                            task.getId()
                    );
                    requestCommandService.handle(createRequestCommand);
                    taskRepository.save(task);
                });
    }

    // ------------------------------------------------------------------
    // LÓGICA DE ALERTA DE PLAZO (NUEVO)
    // ------------------------------------------------------------------
    private void sendDueDateAlerts() {
        // [now] se define aquí para ser consistente durante esta ejecución transaccional
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        // Ventana de control de frecuencia: Solo enviamos si la última alerta fue hace más de 1 hora
        OffsetDateTime oneHourAgo = now.minusHours(1);

        // 1. Alerta de 24 horas (Vence entre 23h y 24h a partir de ahora)
        checkAndSendAlert(now.plusHours(23), now.plusHours(24), 24, now, oneHourAgo);

        // 2. Alerta de 12 horas (Vence entre 11h y 12h a partir de ahora)
        checkAndSendAlert(now.plusHours(11), now.plusHours(12), 12, now, oneHourAgo);

        // 3. Alerta de 6 horas (Vence entre 5h y 6h a partir de ahora)
        checkAndSendAlert(now.plusHours(5), now.plusHours(6), 6, now, oneHourAgo);
    }

    private void checkAndSendAlert(OffsetDateTime alertStart, OffsetDateTime alertEnd, int hours, OffsetDateTime now, OffsetDateTime lastAlertCheckTime) {

        List<Task> tasksToAlert = taskRepository.findAllByStatusAndDueDateBetweenAndLastAlertSentBefore(
                TaskStatus.IN_PROGRESS, alertStart, alertEnd, lastAlertCheckTime
        );

        if (tasksToAlert.isEmpty()) return;

        for (Task task : tasksToAlert) {

            // 1. Crear el mensaje de notificación
            String message = String.format(
                    "¡ALERTA de Plazo! La tarea '%s' vence en aproximadamente %d horas.",
                    task.getTitle(),
                    hours
            );

            // 2. Enviar Notificación
            if (task.getMember() != null) {
                Long memberId = task.getMember().getId();
                CreateNotificationCommand notificationCommand = new CreateNotificationCommand(memberId, message, task.getId());
                notificationCommandService.handle(notificationCommand);
            }

            // 3. Marcar la tarea como alertada para evitar duplicados.
            task.setLastAlertSent(now);
            taskRepository.save(task);
        }
    }
}