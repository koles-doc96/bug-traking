package ru.evgen.bugtraking.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity(name = "task")
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Integer priority;

    @NotNull
    @Column(name = "create_date")
    private Date createTime;

    @Column(name = "edit_date", insertable = false, updatable = false)
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date editTime;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Project project;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    @NotNull
    private Status status;



}
