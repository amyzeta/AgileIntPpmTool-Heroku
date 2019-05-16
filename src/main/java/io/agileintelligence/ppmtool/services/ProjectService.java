package io.agileintelligence.ppmtool.services;

import io.agileintelligence.ppmtool.domain.Project;
import io.agileintelligence.ppmtool.domain.Task;
import io.agileintelligence.ppmtool.exceptions.ValidationException;
import io.agileintelligence.ppmtool.exceptions.ValidationExceptionFactory;
import io.agileintelligence.ppmtool.repositories.ProjectRepository;
import io.agileintelligence.ppmtool.repositories.TaskRepository;
import io.agileintelligence.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    public Project createProject(final Project project, final String username) {
        if (project.getId() != null) {
            throw ValidationExceptionFactory.forId("Do not supply an ID when creating");
        }
        project.setUser(userRepository.findByUsername(username));
        try {
            return projectRepository.save(project);
        } catch (final DataIntegrityViolationException e) {
            throw ValidationExceptionFactory.forProjectIdentifier(String.format("Project identifier '%s' already exists", project.getProjectIdentifier()));
        }
    }

    public Project updateProject(final Project project, final String username) {
        final Project existingProject = getProject(project.getId(), username);
        existingProject.setProjectName(project.getProjectName());
        existingProject.setDescription(project.getDescription());
        existingProject.setStartDate(project.getStartDate());
        existingProject.setEndDate(project.getEndDate());
        projectRepository.save(existingProject);
        entityManager.clear();
        return existingProject;
    }

    public Project getProject(final Long id, final String username) {
        return projectRepository.findById(id)
                .filter(p -> p.getUser().getUsername().equals(username))
                .orElseThrow(() -> ValidationExceptionFactory.forId(String.format("Project id '%s' does not exist or does not belong to this user", id)));
    }

    public Collection<Project> getAllProjects(final String username) {
        return this.userRepository.findByUsername(username).getProjects();
    }

    public void deleteProject(final Long id, final String username) {
        // don't use deleteById because the error message is not so nice when project does not exist
        this.projectRepository.delete(getProject(id, username));
    }

    public Task addTask(final Long id, final Task task, final String username) {
        if (task.getId() != null) {
            throw ValidationExceptionFactory.forId("Do not supply an ID when creating");
        }
        final Project project = getProject(id, username);
        task.setTaskSequence(project.getNextTaskSequence());
        task.setProject(project);
        this.taskRepository.save(task);
        return task;
    }

    public Collection<Task> getTasks(final Long id, final String taskSequence, final String username) {
        getProject(id, username); // will throw exception if project does not exist
        if (taskSequence == null) {
            return this.taskRepository.findByProjectId(id);
        }
        return Optional.ofNullable(this.taskRepository.findByTaskSequence(taskSequence))
                .filter(t -> t.belongsTo(id))
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
    }

    public Task getTask(final Long id, final Long taskId, final String username) {
        getProject(id, username); // will throw exception if project does not exist
        return taskRepository.findById(taskId)
                .filter(t -> t.belongsTo(id))
                .orElseThrow(() -> ValidationExceptionFactory.forId(String.format("Task id '%s' does not exist or is not for project '%s", taskId, id)));
    }

    public Task updateTask(final Long id, final Task task, final String username) {
        final Project project = getProject(id, username); // will throw exception if project does not exist
        getTask(id, task.getId(), username); // will throw exception if task does not exist or is not for this project
        task.setProject(project);
        this.taskRepository.save(task);
        entityManager.clear();
        return getTask(id, task.getId(), username);
    }

    public void deleteTask(final Long id, final Long taskId, final String username) {
        this.taskRepository.delete(getTask(id, taskId, username));
    }
}
