package demo.axon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ CustomerConfig.class, DataConfigJPA.class, CommonConfig.class, IntegrationConfig.class })
public class AppConfig {}
