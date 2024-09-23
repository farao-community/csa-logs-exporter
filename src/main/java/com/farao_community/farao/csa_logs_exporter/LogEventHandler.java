package com.farao_community.farao.csa_logs_exporter;

import com.farao_community.farao.swe_csa.api.resource.RaoRunnerLogsModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class LogEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogEventHandler.class);
    private final TaskRepository taskRepository;

    public LogEventHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Bean
    public Consumer<Flux<List<byte[]>>> consumeLogEvent() {
        return f -> f.subscribe(messages -> {
            try {
                handleTaskEventBatchUpdate(mapMessagesToListEvents(messages));
            } catch (Exception e) {
                LOGGER.error(String.format("Unable to handle task events update properly %s", messages), e);
            }
        });
    }

    private List<RaoRunnerLogsModel> mapMessagesToListEvents(List<byte[]> messages) {
        return messages.stream()
            .map(String::new)
            .map(this::mapMessageToEvent)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private RaoRunnerLogsModel mapMessageToEvent(String messages) {
        try {
            return new ObjectMapper().readValue(messages, RaoRunnerLogsModel.class);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Couldn't parse log event, Impossible to match the event with concerned task", e);
            return null;
        }
    }

    void handleTaskEventBatchUpdate(List<RaoRunnerLogsModel> batch) {
        Map<UUID, Task> storedTasks = new HashMap<>();
        List<Task> tasksToSave = new ArrayList<>();
        for (RaoRunnerLogsModel event : batch) {
            UUID taskId = UUID.fromString(event.getGridcapaTaskId());
            Task task = storedTasks.get(taskId);
            if (task == null) { // first event for task from current batch
                Optional<Task> optionalTask = taskRepository.findById(taskId);
                if (optionalTask.isPresent()) { // task already created
                    task = optionalTask.get();
                    storedTasks.put(taskId, task);
                    updateTaskEvent(event, task, tasksToSave);
                } else { // create new task
                    Task newTask = new Task(taskId, OffsetDateTime.parse(event.getTimestamp()));
                    taskRepository.save(newTask);
                    updateTaskEvent(event, newTask, tasksToSave);
                }
            } else {
                updateTaskEvent(event, task, tasksToSave);
            }
        }
        taskRepository.saveAll(tasksToSave);
        for (Task task : tasksToSave) {
            LOGGER.debug("Task {} events has been added on {}", task.getId(), task.getTimestamp());
        }
    }

    private void updateTaskEvent(RaoRunnerLogsModel loggerEvent, Task task, List<Task> tasksToSave) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(loggerEvent.getTimestamp());
        String message = loggerEvent.getMessage();
        task.addProcessEvent(offsetDateTime, loggerEvent.getLevel(), message, "csa runner or rao runner");
        if (!tasksToSave.contains(task)) {
            tasksToSave.add(task);
        }
    }

}
