
package com.farao_community.farao.csa_logs_exporter;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/csa-logs-exporter")
@CrossOrigin(origins = "*")
public class LogsExporterController {

    private static final String JSON_API_MIME_TYPE = "application/vnd.api+json";

    private final CsaLogsExporterService csaLogsExporterService;

    public LogsExporterController(CsaLogsExporterService csaLogsExporterService) {
        this.csaLogsExporterService = csaLogsExporterService;
    }

    @GetMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, JSON_API_MIME_TYPE})
    public ResponseEntity convertCsaProfilesZipToCsaRequest(@RequestParam String taskId) throws IOException {
        return toFileAttachmentResponse(csaLogsExporterService.getLogsInputStreamByTaskId(taskId).readAllBytes(), "runner.log");
    }

    public static ResponseEntity toFileAttachmentResponse(byte[] fileContent, String fileName) {
        try {
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename(fileName).build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(headers).body(fileContent);
        } catch (Exception e) {
            throw new RuntimeException("Cannot return log file as attachment", e);
        }
    }
}
