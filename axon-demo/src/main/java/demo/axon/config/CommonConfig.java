package demo.axon.config;

import java.util.Arrays;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.SimpleEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class CommonConfig {

    @Autowired
    DataConfig data;
    
    /*
     * CommandBus
     */
    
    @Bean
    @DependsOn({ "springTransactionalInterceptor" })
    public CommandBus commandBus() {
        SimpleCommandBus commandBus = new SimpleCommandBus();
        commandBus.setInterceptors(Arrays.asList(data.springTransactionalInterceptor()));
        return commandBus;
    }

    /*
     * EventBus
     */
    
    @Bean
    public EventBus eventBus() {
        return new SimpleEventBus();
    }
    
}
