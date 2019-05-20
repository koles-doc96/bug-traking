package ru.evgen.bugtraking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.evgen.bugtraking.model.Status;
import ru.evgen.bugtraking.model.Task;
import ru.evgen.bugtraking.repository.ProjectRepository;
import ru.evgen.bugtraking.repository.StatusRepository;
import ru.evgen.bugtraking.repository.TaskRepository;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static ru.evgen.bugtraking.Constant.*;
import static ru.evgen.bugtraking.utils.Util.getStatus;
import static ru.evgen.bugtraking.utils.Util.isColumn;

@RestController
@RequestMapping("/task")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StatusRepository statusRepository;

    /**
     * Получение задачи по id
     *
     * @param id - id задачи
     * @return - задача
     */
    @RequestMapping(value = "/getTask/{id}", method = RequestMethod.GET)
    public ResponseEntity<Task> getTask(@PathVariable Integer id) {
        Task task = taskRepository.findById(id.intValue());
        if (task != null) {
            return new ResponseEntity<>(task,
                    HttpStatus.OK);
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Error", "not found task by id: " + id);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Добавление/редактирование задачи
     *
     * @param task - задача в JSON
     * @return - задача
     */
    @RequestMapping(value = "/addEditTask", method = RequestMethod.POST)
    public ResponseEntity<Task> addEditTask(@RequestBody Task task) {
        HttpHeaders responseHeaders = new HttpHeaders();
        if (task.getId() == null) {
            task.setStatus(newStatus);
            task.setProject(projectRepository.findById(task.getProject().getId().intValue()));
            Task newTask = taskRepository.save(task);
            return new ResponseEntity<>(newTask, HttpStatus.CREATED);
        } else {
            if (!task.getStatus().equals(closeStatus)) {
                Task edit = taskRepository.findById(task.getId().intValue());
                edit.setProject(projectRepository.findById(task.getProject().getId().intValue()));
                edit.setStatus(edit.getStatus().setStatus(statusRepository.getById(task.getStatus().getId())));
                int priority = task.getPriority();
                if (priority < 0) {
                    responseHeaders.set("Error", "Priority must be an integer");
                    return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
                }
                edit.setPriority(priority);
                taskRepository.save(edit);
                return new ResponseEntity<>(edit, HttpStatus.OK);
            }
            responseHeaders.set("Error", "Task is closed, change is impossible");
            return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_MODIFIED);
        }
    }

    /**
     * Изменение статуса задачи
     *
     * @param id     - задача
     * @param status - Тектовое представление статуса (NEW,WORK,CLOSE)
     * @return - Измененная задача
     */
    @RequestMapping(value = "/setStatus/{id}/{status}", method = RequestMethod.PUT)
    public ResponseEntity<Task> setStatus(@PathVariable Integer id, @PathVariable String status) {
        Task task = taskRepository.findById(id.intValue());
        HttpHeaders responseHeaders = new HttpHeaders();
        if (task != null) {
            Status editStatus = getStatus(status);
            if (editStatus != null) {
                task.setStatus(task.getStatus().setStatus(editStatus));
                return new ResponseEntity<>(task, HttpStatus.OK);
            }
            responseHeaders.set("Error", "not found status: " + status + " Example : NEW,WORK,CLOSE");
            return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
        }
        responseHeaders.set("Error", "not found task by id: " + id);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Удаление задачи по id
     *
     * @return загололовок
     */
    @RequestMapping(value = "/delTask/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Task> delTask(@PathVariable Integer id) {
        Task task = taskRepository.findById(id.intValue());
        HttpHeaders responseHeaders = new HttpHeaders();
        if (task != null) {
            taskRepository.delete(task);
            responseHeaders.set("Info", "Task by id: " + id + " is deleted");
            return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
        }
        responseHeaders.set("Error", "not found task by id: " + id);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Просмотр задач проекта с возможностью сортировки
     *
     * @param id     проект
     * @param size   - количество задач. опциональный  параметр
     * @param column - по какому полю будет сортировка. опциональный  параметр
     * @return - задачи  выбранного проекта
     */
    @RequestMapping(value = "/getTasks", method = RequestMethod.GET)
    public ResponseEntity<List<Task>> getTasks(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "column", required = false) String column) {
        HttpHeaders responseHeaders = new HttpHeaders();

        if (!StringUtils.isEmpty(column) && !isColumn(column, Task.class)) {
            responseHeaders.set("Error", "No property " + column + " found for type Task ");
            return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
        }

        Pageable page = new PageRequest(
                0,
                size != null ? size : 10,
                Sort.Direction.ASC,
                column.isEmpty() ? "name" : column);

        List<Task> tasks = taskRepository.findByProject(projectRepository.findById(id.intValue()), page);
        if (!CollectionUtils.isEmpty(tasks)) {
            log.info(tasks.toString());
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }
        responseHeaders.set("Error", "not found task by id: " + id);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Просмотр задач проекта с фозможностью фильтрации по диапазону дат
     *
     * @param id   проект
     * @param size количество задач выведенных на экран, опциональный параметр
     * @param from - начало диапазона
     * @param to   - конец диапазона
     * @return задачи  выбранного проекта
     */
    @RequestMapping(value = "/getFilterDateTasks", method = RequestMethod.GET)
    public ResponseEntity<List<Task>> getFilterTasks(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "from") String from,
            @RequestParam(value = "to") String to) {
        HttpHeaders responseHeaders = new HttpHeaders();
        Date dateFrom;
        Date dateTo;
        try {
            dateFrom = new SimpleDateFormat("yyyy-MM-dd").parse(from);
            dateTo = new SimpleDateFormat("yyyy-MM-dd").parse(to);
        } catch (ParseException e) {
            log.error(e.getMessage());
            responseHeaders.set("Error", "Date format not support. Example yyyy-MM-dd");
            return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
        }

        Pageable page = new PageRequest(0, size != null ? size : 10);
        List<Task> tasks = taskRepository.queryBetweenDate(dateFrom, dateTo, page);
        if (!CollectionUtils.isEmpty(tasks)) {
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }
        responseHeaders.set("Error", "not found task by id: " + id);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Просмотр задач проекта с фозможностью фильтрации по приоритету
     * @param id   проект
     * @param size количество задач выведенных на экран, опциональный параметр
     * @param priority приоритет
     * @return задачи  выбранного проекта
     */
    @RequestMapping(value = "/getFilterPriorityTasks", method = RequestMethod.GET)
    public ResponseEntity<List<Task>> getFilterPriorityTasks(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "priority") Integer priority) {
        HttpHeaders responseHeaders = new HttpHeaders();
        Pageable page = new PageRequest(0, size != null ? size : 10);
        List<Task> tasks = taskRepository.findByPriority(priority, page);
        if (!CollectionUtils.isEmpty(tasks)) {
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        }
        responseHeaders.set("Error", "not found task by id: " + id + "and priority: " + priority);
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
    }

    /**
     * Просмотр задач проекта с фозможностью фильтрации по статусу
     * @param id   проект
     * @param size количество задач выведенных на экран, опциональный параметр
     * @param status статус
     * @return задачи  выбранного проекта
     */
    @RequestMapping(value = "/getFilterStatusTasks", method = RequestMethod.GET)
    public ResponseEntity<List<Task>> getFilterStatusTasks(
            @RequestParam(value = "id") Integer id,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "status") String status) {
        HttpHeaders responseHeaders = new HttpHeaders();
        Pageable page = new PageRequest(0, size != null ? size : 10);
        Status editStatus = getStatus(status);
        if (editStatus != null) {
            List<Task> tasks = taskRepository.findByStatus(editStatus, page);
            if (!CollectionUtils.isEmpty(tasks)) {
                return new ResponseEntity<>(tasks, HttpStatus.OK);
            }
            responseHeaders.set("Error", "not found task by id: " + id + "and status: " + status);
            return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);
        }
        responseHeaders.set("Error", "not found status: " + status + " Example : NEW,WORK,CLOSE");
        return new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND);

    }

}
