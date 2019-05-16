package io.agileintelligence.ppmtool.web;

import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.domain.Task;
import io.agileintelligence.ppmtool.exceptions.ValidationExceptionFactory;
import io.agileintelligence.ppmtool.services.ProjectService;
import io.agileintelligence.ppmtool.services.ValidationErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {

    @Autowired
    private ProjectService projectService;


    @Autowired
    private ValidationErrorService validationErrorService;

    @PostMapping("")
    public ResponseEntity<?> create(@Valid @RequestBody final Project project, final BindingResult result, final Principal principal) {
        final Optional<ResponseEntity<?>> responseEntity = this.validationErrorService.validationErrorMessage(result);
        return responseEntity.orElseGet(() -> new ResponseEntity<>(projectService.createProject(project, principal.getName()), HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable final Long id, @Valid @RequestBody final Project project, final BindingResult result, final Principal principal) {
        return this.validationErrorService.validationErrorMessage(result).orElseGet(() -> {
            if (project.getId() == null) {
                project.setId(id);
            } else if (!project.getId().equals(id)) {
                throw ValidationExceptionFactory.forId("id in path and body do not match");
            }
            return new ResponseEntity<>(projectService.updateProject(project, principal.getName()), HttpStatus.OK);
        });

    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> get(@PathVariable final Long id, final Principal principal) {
        final Project project = projectService.getProject(id, principal.getName());
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Collection<Project>> getAll(final Principal principal) {
        return new ResponseEntity<>(projectService.getAllProjects(principal.getName()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable final Long id, final Principal principal) {
        projectService.deleteProject(id, principal.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/task")
    public ResponseEntity<?> addTask(@PathVariable final Long id, @Valid @RequestBody final Task task, final BindingResult result, final Principal principal) {
        return this.validationErrorService.validationErrorMessage(result)
                .orElseGet(() -> new ResponseEntity<Task>(projectService.addTask(id, task, principal.getName()), HttpStatus.CREATED));
    }

    @GetMapping("{id}/task")
    public ResponseEntity<Collection<Task>> getTasksForProject(@PathVariable final Long id, @RequestParam(required=false) final String taskSequence, final Principal principal) {
        return new ResponseEntity<>(projectService.getTasks(id, taskSequence, principal.getName()), HttpStatus.OK);
    }
    @GetMapping("/{id}/task/{taskId}")
    public ResponseEntity<Task> get(@PathVariable final Long id, @PathVariable final Long taskId, final Principal principal) {
        return new ResponseEntity<>(projectService.getTask(id, taskId, principal.getName()), HttpStatus.OK);
    }

    @PutMapping("/{id}/task/{taskId}")
    public ResponseEntity<?> update(@PathVariable final Long id, @PathVariable final Long taskId, @Valid @RequestBody final Task task, final BindingResult result, final Principal principal) {
        return this.validationErrorService.validationErrorMessage(result).orElseGet(() -> {
            if (task.getId() == null) {
                task.setId(taskId);
            } else if (!task.getId().equals(taskId)) {
                throw ValidationExceptionFactory.forId("id in path and body do not match");
            }
            return new ResponseEntity<>(projectService.updateTask(id, task, principal.getName()), HttpStatus.OK);
        });
    }

    @DeleteMapping("/{id}/task/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable final Long id, @PathVariable final Long taskId, final Principal principal) {
        projectService.deleteTask(id, taskId, principal.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
