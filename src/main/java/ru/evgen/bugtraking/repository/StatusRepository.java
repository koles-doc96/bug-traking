package ru.evgen.bugtraking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.evgen.bugtraking.model.Status;

public interface StatusRepository extends JpaRepository<Status,Integer> {
    Status getById(int id);
}
