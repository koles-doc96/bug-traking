package ru.evgen.bugtraking.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static ru.evgen.bugtraking.Constant.*;

@Data
@Entity(name = "status")
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    public Status() {
    }

    public Status(Integer id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * o	Если задача имеет статус «НОВАЯ», её статус может быть изменён на «В РАБОТЕ» и «ЗАКРЫТА». .
     * o	Если задача имеет статус «В РАБОТЕ», её статус может быть изменён на «НОВАЯ» и «ЗАКРЫТА». .
     * o	Если задача имеет статус «ЗАКРЫТА», модификация становится недопустимой.
     * @param edit - измененный статус
     * @return - статус
     */
    public Status setStatus(Status edit) {
        if(this.equals(closeStatus)) {
            return this;
        }
        return edit;
    }
}
