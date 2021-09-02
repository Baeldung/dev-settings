package com.baeldung.lsso.service.impl;

import com.baeldung.lsso.persistence.model.Task;
import com.baeldung.lsso.persistence.repository.ITaskRepository;
import com.baeldung.lsso.service.ITaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements ITaskService {

    private ITaskRepository taskRepository;

    public TaskServiceImpl(ITaskRepository projectRepository) {
        this.taskRepository = projectRepository;
    }

    @Override
    public Iterable<Task> findByProjectId(Long id) {
        return taskRepository.findByProjectId(id);
    }
}
