package demo.axon.config;

import org.axonframework.domain.Event;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventListener;
import org.axonframework.integration.adapter.EventListeningMessageChannelAdapter;
import org.axonframework.integration.eventbus.SpringIntegrationEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.SubscribableChannel;

@Configuration
public class IntegrationConfig {

    @Autowired
    CommonConfig common;

    @Bean
    SubscribableChannel channel() {
        final SyncTaskExecutor executor = new SyncTaskExecutor();
        // SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

        final ExecutorChannel channel = new ExecutorChannel(executor);
        return channel;
    }

    /*
     * eventBus listener
     */

    @Bean
    @DependsOn({ "eventBus", "channel" })
    EventListener eventListeningChannel() {
        final EventListeningMessageChannelAdapter eventListeningChannel = new EventListeningMessageChannelAdapter(
            common.eventBus(), channel());

        return eventListeningChannel;
    }

    /*
     * integration publishing
     */

    /*@Bean
    public EventBus integrationEventBus() {
        return new SimpleEventBus();
    }

    @Bean
    @DependsOn("integrationEventBus")
    MessageHandler messageHandler() {
        EventPublishingMessageChannelAdapter eventPublishingChannel =
            new EventPublishingMessageChannelAdapter(integrationEventBus());

        return eventPublishingChannel;
    }

    @Bean
    @DependsOn({ "channel", "messageHandler" })
    AbstractEndpoint endpoint() {
        EventDrivenConsumer consumer =
            new EventDrivenConsumer(channel(), messageHandler());
        return consumer;
    }

    @Bean
    @DependsOn("integrationEventBus")
    EventListener eventListener() {
        EventListener eventListener = new EventListener() {
            @Override
            public void handle(Event event) {
                System.err.println(String.format("Received event of type [%s] at [%s]",
                    event.getClass().getSimpleName(), event.getTimestamp().toString()));
            }
        };

        integrationEventBus().subscribe(eventListener);
        return eventListener;
    }*/

    /*
     * integration publishing 2
     */

    @Bean
    EventListener eventListener() {
        final EventListener eventListener = new EventListener() {
            @Override
            public void handle(final Event event) {
                System.err.println(String.format("Received event of type [%s] at [%s]", event
                    .getClass().getSimpleName(), event.getTimestamp().toString()));
            }
        };

        return eventListener;
    }

    @Bean
    @DependsOn({ "channel", "eventListener" })
    public EventBus integrationEventBus() {
        final SpringIntegrationEventBus eventBus = new SpringIntegrationEventBus();
        eventBus.setChannel(channel());
        eventBus.subscribe(eventListener());
        return eventBus;
    }

}
