package com.farao_community.farao.csa_logs_exporter;

import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

@Entity
public class Task implements Serializable {

    @Id
    private UUID id;

    private OffsetDateTime timestamp;

    @OneToMany(
        cascade = {CascadeType.MERGE, CascadeType.PERSIST},
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    @SortNatural
    private final SortedSet<ProcessEvent> processEvents = Collections.synchronizedSortedSet(new TreeSet<>());

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public SortedSet<ProcessEvent> getProcessEvents() {
        return processEvents;
    }

    public Task() {
    }

    public Task(UUID uuid, OffsetDateTime timestamp) {
        this.id = uuid;
        this.timestamp = timestamp;
    }

    public void addProcessEvent(OffsetDateTime timestamp, String level, String message, String serviceName) {
        processEvents.add(new ProcessEvent(timestamp, level, message, serviceName));
    }
}
