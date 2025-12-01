package nrg.inc.synhubbackend.shared.application.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service that periodically calls the health endpoint to keep the application alive.
 * This is useful for preventing cloud platforms from putting the application to sleep.
 */
@Service
public class KeepAliveService {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveService.class);

    private final RestTemplate restTemplate;
    private final String healthEndpointUrl;

    public KeepAliveService(
            RestTemplate restTemplate,
            @Value("${app.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.healthEndpointUrl = baseUrl + "/api/v1/actuator/health";
    }

    /**
     * Calls the health endpoint every 5 minutes to keep the application alive.
     * The schedule is defined using a cron expression: every 5 minutes.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    public void performHealthCheck() {
        try {
            logger.info("Performing keep-alive health check...");
            String response = restTemplate.getForObject(healthEndpointUrl, String.class);
            logger.info("Health check successful: {}", response);
        } catch (Exception e) {
            logger.error("Health check failed: {}", e.getMessage());
        }
    }
}

