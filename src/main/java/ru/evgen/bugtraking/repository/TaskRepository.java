package ru.evgen.bugtraking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.evgen.bugtraking.model.Project;
import ru.evgen.bugtraking.model.Status;
import ru.evgen.bugtraking.model.Task;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Task findById(int id);

    List<Task> findByProject(Project project, Pageable page);

    @Query(value = "SELECT * from bugtraking.task t WHERE t.create_date BETWEEN :from and :to", nativeQuery = true)
    List<Task> queryBetweenDate(@Param("from") Date from, @Param("to") Date to, Pageable page);

    List<Task> findByPriority(int priority, Pageable page);

    List<Task> findByStatus(Status status, Pageable page);

}
