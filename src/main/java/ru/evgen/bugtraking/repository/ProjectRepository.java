package ru.evgen.bugtraking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.evgen.bugtraking.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Project findById(int id);

}
