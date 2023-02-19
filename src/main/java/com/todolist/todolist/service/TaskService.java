package com.todolist.todolist.service;

import com.todolist.todolist.dao.entity.Task;
import com.todolist.todolist.dao.entity.User;
import com.todolist.todolist.dao.repository.TaskRepository;
import com.todolist.todolist.dao.repository.UserRepository;
import com.todolist.todolist.exception.TaskNotFoundException;
import com.todolist.todolist.exception.UserNotFoundException;
import com.todolist.todolist.model.enums.TaskStatus;
import com.todolist.todolist.util.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public List<Task> getTasksByUserId(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(ExceptionUtil::exUserNotFound);
        return user.getTasks();
    }

    public Task createTaskById(Long userId, Task task) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(ExceptionUtil::exUserNotFound);
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task newTask) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTaskName(newTask.getTaskName());
                    task.setTaskStatus(newTask.getTaskStatus());
                    task.setDescription(newTask.getDescription());
                    task.setPhoto(newTask.getPhoto());
                    task.setTaskSortType(newTask.getTaskSortType());
                    task.setTaskDeadlineDate(newTask.getTaskDeadlineDate());
                    return taskRepository.save(task);
                })
                .orElseThrow(ExceptionUtil::exTaskNotFound);
    }

    public List<Task> getArchiveTasks(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.orElseThrow(ExceptionUtil::exUserNotFound);
        return user.getTasks().stream()
                .filter(this::isTaskStatusArchived)
                .collect(Collectors.toList());

    }

    private boolean isTaskStatusArchived(Task task) {
        if (Objects.isNull(task)) return false;
        return TaskStatus.ARCHIVE.equals(task.getTaskStatus());
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }


}
