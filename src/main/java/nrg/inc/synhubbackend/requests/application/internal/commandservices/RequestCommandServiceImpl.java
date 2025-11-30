package nrg.inc.synhubbackend.requests.application.internal.commandservices;

import nrg.inc.synhubbackend.Notification.domain.model.commands.CreateNotificationCommand;
import nrg.inc.synhubbackend.Notification.domain.model.services.NotificationCommandService;
import nrg.inc.synhubbackend.requests.domain.model.aggregates.Request;
import nrg.inc.synhubbackend.requests.domain.model.commands.CreateRequestCommand;
import nrg.inc.synhubbackend.requests.domain.model.commands.DeleteAllRequestsByTaskIdCommand;
import nrg.inc.synhubbackend.requests.domain.model.commands.DeleteRequestCommand;
import nrg.inc.synhubbackend.requests.domain.model.commands.UpdateRequestCommand;
import nrg.inc.synhubbackend.requests.domain.model.valueobjects.RequestType;
import nrg.inc.synhubbackend.requests.domain.services.RequestCommandService;
import nrg.inc.synhubbackend.requests.infrastructure.persistence.jpa.repositories.RequestRepository;
import nrg.inc.synhubbackend.tasks.interfaces.acl.TasksContextFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RequestCommandServiceImpl implements RequestCommandService {

    private final RequestRepository requestRepository;
    private final TasksContextFacade tasksContextFacade;
    private final NotificationCommandService notificationCommandService; // [NUEVO CAMPO]

    public RequestCommandServiceImpl(
            RequestRepository requestRepository,
            TasksContextFacade tasksContextFacade,
            NotificationCommandService notificationCommandService) { // [NUEVO PARÁMETRO]
        this.requestRepository = requestRepository;
        this.tasksContextFacade = tasksContextFacade;
        this.notificationCommandService = notificationCommandService; // [ASIGNACIÓN]
    }

    @Override
    public Long handle(CreateRequestCommand command) {
        try {
            RequestType.fromString(command.requestType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid request type");
        }

        var task = this.tasksContextFacade.getTaskById(command.taskId());

        if (task.isEmpty())
            throw new IllegalArgumentException("Task with id " + command.taskId() + " does not exist");

        var request = new Request(command);
        request.setTask(task.get());
        this.requestRepository.save(request);
        return request.getId();
    }

    @Override
    public Optional<Request> handle(UpdateRequestCommand command) {
        var requestId = command.requestId();

        if (!this.requestRepository.existsById(requestId))
            throw new IllegalArgumentException("Request with id " + requestId + " does not exist");

        var requestToUpdate = this.requestRepository.findById(requestId).get();
        requestToUpdate.updateRequestStatus(command.requestStatus());

        try {
            var updatedRequest = this.requestRepository.save(requestToUpdate);

            // [LÓGICA DE NOTIFICACIÓN INTEGRADA]
            var task = updatedRequest.getTask();
            var member = task.getMember(); // Obtenemos el Miembro de la Tarea

            if (member != null) {
                Long memberId = member.getId();
                String requestType = updatedRequest.getRequestType(); // Tipo de Request
                String status = command.requestStatus().toUpperCase();
                String taskTitle = task.getTitle();

                String message = String.format(
                        "Su solicitud de %s para la tarea '%s' ha sido %s.",
                        requestType,
                        taskTitle,
                        status
                );

                var notificationCommand = new CreateNotificationCommand(memberId, message, task.getId());
                this.notificationCommandService.handle(notificationCommand);
            }
            // [FIN LÓGICA DE NOTIFICACIÓN INTEGRADA]

            return Optional.of(updatedRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while updating request: " + e.getMessage());
        }
    }

    @Override
    public void handle(DeleteRequestCommand command) {
        var requestId = command.requestId();

        if (!requestRepository.existsById(requestId))
            throw new IllegalArgumentException("Request with id " + requestId + " does not exist");

        try {
            requestRepository.deleteById(requestId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while deleting request: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handle(DeleteAllRequestsByTaskIdCommand command) {
        var taskId = command.taskId();

        if (this.tasksContextFacade.getTaskById(taskId).isEmpty())
            throw new IllegalArgumentException("Task with id " + taskId + " does not exist");

        try {
            this.requestRepository.deleteByTaskId(taskId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while deleting requests for task: " + e.getMessage());
        }
    }
}