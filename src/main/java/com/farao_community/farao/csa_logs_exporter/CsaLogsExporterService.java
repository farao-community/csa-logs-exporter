package com.farao_community.farao.csa_logs_exporter;

import com.farao_community.farao.csa_logs_exporter.s3.S3ArtifactsAdapter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CsaLogsExporterService {

    private final TaskRepository taskRepository;
    private final S3ArtifactsAdapter s3ArtifactsAdapter;

    public CsaLogsExporterService(TaskRepository taskRepository, S3ArtifactsAdapter s3ArtifactsAdapter) {
        this.taskRepository = taskRepository;
        this.s3ArtifactsAdapter = s3ArtifactsAdapter;
    }

    public InputStream getLogsInputStreamByTaskId(String taskId) {
        Task task = taskRepository.getById(UUID.fromString(taskId));
        SortedSet<ProcessEvent> events = task.getProcessEvents();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (ProcessEvent event : events) {
            baos.writeBytes(event.toString().getBytes(StandardCharsets.UTF_8));
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private void getLogFileByTaskId(String taskId) {
        s3ArtifactsAdapter.uploadFile("", getLogsInputStreamByTaskId(taskId));
    }
}
