package nrg.inc.synhubbackend.Notification.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nrg.inc.synhubbackend.Notification.domain.model.commands.MarkAsReadCommand;
import nrg.inc.synhubbackend.Notification.domain.model.queries.GetAllNotificationsByMemberIdQuery;
import nrg.inc.synhubbackend.Notification.domain.model.services.NotificationCommandService;
import nrg.inc.synhubbackend.Notification.domain.model.services.NotificationQueryService;
import nrg.inc.synhubbackend.Notification.interfaces.rest.resources.NotificationResource;
import nrg.inc.synhubbackend.Notification.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import nrg.inc.synhubbackend.tasks.domain.model.queries.GetMemberByUsernameQuery;
import nrg.inc.synhubbackend.tasks.domain.services.MemberQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member/notifications")
@Tag(name = "Member Notifications", description = "Endpoints para la gestión de notificaciones del miembro")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;
    private final MemberQueryService memberQueryService; // Se usa para obtener el MemberId del usuario autenticado

    public NotificationController(
            NotificationQueryService notificationQueryService,
            NotificationCommandService notificationCommandService,
            MemberQueryService memberQueryService) {
        this.notificationQueryService = notificationQueryService;
        this.notificationCommandService = notificationCommandService;
        this.memberQueryService = memberQueryService;
    }

    @GetMapping
    @Operation(summary = "Obtiene todas las notificaciones del miembro autenticado (lista flotante)")
    public ResponseEntity<List<NotificationResource>> getAllNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        // 1. Obtener el MemberId del usuario autenticado
        var memberOpt = memberQueryService.handle(new GetMemberByUsernameQuery(userDetails.getUsername()));
        if (memberOpt.isEmpty()) return ResponseEntity.notFound().build();

        Long memberId = memberOpt.get().getId();

        // 2. Ejecutar la Query del Dominio
        var query = new GetAllNotificationsByMemberIdQuery(memberId);
        var notifications = notificationQueryService.handle(query);

        // 3. Transformar a Resource
        var resources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @PatchMapping("/{notificationId}/mark-read")
    @Operation(summary = "Marca una notificación específica como leída.")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationCommandService.handle(new MarkAsReadCommand(notificationId));
        return ResponseEntity.noContent().build();
    }
}