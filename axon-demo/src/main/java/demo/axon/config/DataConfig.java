package demo.axon.config;

import org.axonframework.commandhandling.interceptors.SpringTransactionalInterceptor;
import org.axonframework.eventstore.SnapshotEventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public interface DataConfig {

    @Bean
    SpringTransactionalInterceptor springTransactionalInterceptor();
    
    @Bean
    SnapshotEventStore snapshotEventStore();
}
