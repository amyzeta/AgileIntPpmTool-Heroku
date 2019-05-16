package io.agileintelligence.ppmtool.repositories;

import io.agileintelligence.ppmtool.domain.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findByProjectId(Long id);

    Task findByTaskSequence(String taskSequence);


    @Transactional
    @Modifying
    @Query("update Task t set t.summary = ?1, t.acceptanceCriteria = ?2, t.status = ?3, t.priority = ?4, t.dueDate = ?5, t.updatedAt = current_timestamp where t.id = ?6")
    int updateTask(String summary, String acceptanceCriteria, String status, Integer priority, Date dueDate, Long id);

}
