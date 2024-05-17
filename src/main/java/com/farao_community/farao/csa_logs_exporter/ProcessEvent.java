package com.farao_community.farao.csa_logs_exporter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
public class ProcessEvent implements Comparable<ProcessEvent> {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "level")
    private String level;

    @Column(name = "timestamp")
    private OffsetDateTime timestamp;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "serviceName")
    private String serviceName;

    public ProcessEvent() {
    }

    public ProcessEvent(OffsetDateTime timestamp, String level, String message, String serviceName) {
        this.id = UUID.randomUUID();
        this.timestamp = timestamp;
        this.level = level;
        this.message = message;
        this.serviceName = serviceName;
    }

    public UUID getId() {
        return id;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public int compareTo(ProcessEvent o) {
        if (this.timestamp.isEqual(o.getTimestamp())) {
            return this.message.compareTo(o.getMessage());
        }
        return this.timestamp.compareTo(o.getTimestamp());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProcessEvent that = (ProcessEvent) o;
        return this.id.equals(that.id) && this.level.equals(that.level) && this.timestamp.equals(that.timestamp) && this.message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.level, this.timestamp, this.message, this.serviceName);
    }

    public String toString() {
        return timestamp + " " + level + " " + message + System.lineSeparator();
    }
}
