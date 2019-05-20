package ru.evgen.bugtraking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import ru.evgen.bugtraking.model.Project;
import ru.evgen.bugtraking.repository.ProjectRepository;
import ru.evgen.bugtraking.repository.TaskRepository;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;

    /**
     * Получить проект по id
     *
     * @param id - проекта
     * @return - проект
     */
    @RequestMapping(value = "/getProject/{id}", method = RequestMethod.GET)
    public ResponseEntity<Project> getProject(@PathVariable Integer id) {

        Project project = projectRepository.findById(id.intValue());
        if (project != null) {
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Error", "not found Project by id");
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Добавлени нового проекта
     *
     * @param project - проект
     * @return
     */
    @RequestMapping(value = "/addAndEditProject", method = RequestMethod.POST)
    public ResponseEntity<Project> addAndEditProject(@RequestBody Project project) {
        Project addProject = projectRepository.save(project);
        log.info(addProject.toString());
        return new ResponseEntity<>(project, HttpStatus.CREATED);
    }

    /**
     * Удаление проекта и соотвествующих ему задач
     * @param id проекта
     * @return заголовок
     */
    @RequestMapping(value = "/delProject/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Project> delProject(@PathVariable Integer id) {
        Project project = projectRepository.findById(id.intValue());
        HttpHeaders responseHeaders = new HttpHeaders();

        if (project != null) {
            projectRepository.delete(project);
            responseHeaders.set("Error", "This project deleted");
            return new ResponseEntity<>(null,responseHeaders,HttpStatus.OK);
        }
        responseHeaders.set("Error", "not found Project by id " + id);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/getProjects", method = RequestMethod.GET)
    public ResponseEntity<List<Project>> getProjects(){
        HttpHeaders responseHeaders = new HttpHeaders();
        List<Project> projects = projectRepository.findAll();
        if (!CollectionUtils.isEmpty(projects)) {
            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
        responseHeaders.set("Error", "not found project");
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

}
