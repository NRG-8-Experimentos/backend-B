package nrg.inc.synhubbackend.tasks.infrastructure.persistence.jpa.repositories;

import nrg.inc.synhubbackend.tasks.domain.model.aggregates.Task;
import nrg.inc.synhubbackend.tasks.domain.model.valueobjects.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByMember_Id(Long memberId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByGroup_Id(Long groupId);
    List<Task> findAllByStatusAndDueDateBefore(TaskStatus status, OffsetDateTime dueDate);
    List<Task> findAllByStatusAndDueDateBetweenAndLastAlertSentBefore(
            TaskStatus status, OffsetDateTime startDate, OffsetDateTime endDate, OffsetDateTime lastAlertCheckTime);
}
