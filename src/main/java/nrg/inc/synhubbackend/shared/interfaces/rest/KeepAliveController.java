package nrg.inc.synhubbackend.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import nrg.inc.synhubbackend.shared.interfaces.rest.resources.MessageResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Controller for keep-alive functionality.
 * Provides endpoints to manually trigger health checks and view status.
 */
@RestController
@RequestMapping(value = "/api/v1/keep-alive")
@Tag(name = "Keep Alive", description = "Keep-alive and health check endpoints")
public class KeepAliveController {

    private final RestTemplate restTemplate;
    private final String healthEndpointUrl;

    public KeepAliveController(
            RestTemplate restTemplate,
            @Value("${app.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.healthEndpointUrl = baseUrl + "/api/v1/actuator/health";
    }

    @GetMapping("/ping")
    @Operation(summary = "Manual health check",
               description = "Manually trigger a health check call to the actuator endpoint")
    public ResponseEntity<MessageResource> manualHealthCheck() {
        try {
            String response = restTemplate.getForObject(healthEndpointUrl, String.class);
            return ResponseEntity.ok(
                new MessageResource("Health check successful: " + response)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                new MessageResource("Health check failed: " + e.getMessage())
            );
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Get keep-alive status",
               description = "Returns the status of the keep-alive service")
    public ResponseEntity<MessageResource> getStatus() {
        return ResponseEntity.ok(
            new MessageResource("Keep-alive service is running. Health endpoint: " + healthEndpointUrl)
        );
    }
}

