package nrg.inc.synhubbackend.Notification.domain.model.aggregates;
import jakarta.persistence.Entity;
import lombok.Setter;
import lombok.Getter;
import nrg.inc.synhubbackend.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Entity
@Getter
@Setter
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    private Long memberId;
    private String message;
    private Long relatedTaskId;
    private boolean isRead = false;

    public Notification() {}

    public Notification(Long memberId, String message, Long relatedTaskId) {
        this.memberId = memberId;
        this.message = message;
        this.relatedTaskId = relatedTaskId;
    }
}