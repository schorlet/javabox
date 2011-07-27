package demo.axon.config;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang.UnhandledException;
import org.axonframework.commandhandling.interceptors.SpringTransactionalInterceptor;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.EventCountSnapshotterTrigger;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.SpringAggregateSnapshotter;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.eventstore.fs.FileSystemEventStore;
import org.axonframework.util.SynchronousTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.UrlResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataConfigFile implements DataConfig {

    /*
     * eventStore
     */
    
    @Bean
    @Override
    public SnapshotEventStore snapshotEventStore() {
        FileSystemEventStore eventStore = new FileSystemEventStore();
        try {
            eventStore.setBaseDir(new UrlResource("file:target/eventStore/"));
        } catch (MalformedURLException e) {
            throw new UnhandledException(e);
        }
        return eventStore;
    }

    // @Bean (let commented)
    Snapshotter snapshotter(ApplicationContext context) {
        SpringAggregateSnapshotter snapshotter = new SpringAggregateSnapshotter();
        snapshotter.setTransactionManager(platformTransactionManager());
        snapshotter.setEventStore(snapshotEventStore());
        snapshotter.setExecutor(SynchronousTaskExecutor.INSTANCE);
        
        Map<String, AggregateFactory> factories = context.getBeansOfType(AggregateFactory.class);
        snapshotter.setAggregateFactories(new ArrayList(factories.values()));
        
        return snapshotter;
    }
    
    @Bean
    @DependsOn({ "snapshotEventStore", "platformTransactionManager" })
    @Autowired
    EventStore eventStore(ApplicationContext context) {
        EventCountSnapshotterTrigger snapshotterTrigger = new EventCountSnapshotterTrigger();
        snapshotterTrigger.setEventStore(snapshotEventStore());
        snapshotterTrigger.setSnapshotter(snapshotter(context));
        snapshotterTrigger.setDefaultTrigger(2);
        return snapshotterTrigger;
    }

    /*
     * dataStore
     */
    
    @Bean
    EntityManagerFactory dataStore() {
        EntityManagerFactory entityManagerFactory = Persistence
            .createEntityManagerFactory("dataStore");
        return entityManagerFactory;
    }

    @Bean
    @DependsOn("dataStore")
    PlatformTransactionManager platformTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager(
            dataStore());
        return transactionManager;
    }

    @Bean
    @DependsOn("platformTransactionManager")
    @Override
    public SpringTransactionalInterceptor springTransactionalInterceptor() {
        SpringTransactionalInterceptor springTransactionalInterceptor = new SpringTransactionalInterceptor();
        springTransactionalInterceptor.setTransactionManager(platformTransactionManager());
        return springTransactionalInterceptor;
    }

}
