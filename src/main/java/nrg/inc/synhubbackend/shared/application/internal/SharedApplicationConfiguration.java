package nrg.inc.synhubbackend.shared.application.internal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for shared application beans.
 */
@Configuration
public class SharedApplicationConfiguration {

    /**
     * Provides a RestTemplate bean for making HTTP requests.
     *
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

