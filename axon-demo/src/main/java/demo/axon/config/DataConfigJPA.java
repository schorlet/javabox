package demo.axon.config;

import java.util.ArrayList;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.axonframework.commandhandling.interceptors.SpringTransactionalInterceptor;
import org.axonframework.eventsourcing.AggregateFactory;
import org.axonframework.eventsourcing.EventCountSnapshotterTrigger;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.SpringAggregateSnapshotter;
import org.axonframework.eventstore.EventStore;
import org.axonframework.eventstore.SnapshotEventStore;
import org.axonframework.eventstore.jpa.JpaEventStore;
import org.axonframework.util.SynchronousTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataConfigJPA implements DataConfig {

    /*
     * eventStore
     */

    @Bean
    @Override
    public SnapshotEventStore snapshotEventStore() {
        final JpaEventStore eventStore = new JpaEventStore();
        return eventStore;
    }

    // @Bean (let commented)
    Snapshotter snapshotter(final ApplicationContext context) {
        final SpringAggregateSnapshotter snapshotter = new SpringAggregateSnapshotter();
        snapshotter.setTransactionManager(platformTransactionManager());
        snapshotter.setEventStore(snapshotEventStore());
        snapshotter.setExecutor(SynchronousTaskExecutor.INSTANCE);

        final Map<String, AggregateFactory> factories = context
            .getBeansOfType(AggregateFactory.class);
        snapshotter.setAggregateFactories(new ArrayList(factories.values()));

        return snapshotter;
    }

    @Bean
    @DependsOn({ "snapshotEventStore", "platformTransactionManager" })
    @Autowired
    EventStore eventStore(final ApplicationContext context) {
        final EventCountSnapshotterTrigger snapshotterTrigger = new EventCountSnapshotterTrigger();
        snapshotterTrigger.setEventStore(snapshotEventStore());
        snapshotterTrigger.setSnapshotter(snapshotter(context));
        snapshotterTrigger.setDefaultTrigger(2);
        return snapshotterTrigger;
    }

    /*
     * dataStore
     */

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(false);
        jpaVendorAdapter.setGenerateDdl(false);
        return jpaVendorAdapter;
    }

    @Bean
    @DependsOn("jpaVendorAdapter")
    AbstractEntityManagerFactoryBean entityManagerFactory() {
        final LocalEntityManagerFactoryBean entityManagerFactory = new LocalEntityManagerFactoryBean();
        entityManagerFactory.setPersistenceUnitName("dataStore");
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
        return entityManagerFactory;
    }

    @Bean
    @DependsOn("entityManagerFactory")
    PlatformTransactionManager platformTransactionManager() {
        final EntityManagerFactory nativeEntityManagerFactory = entityManagerFactory().getObject();
        final JpaTransactionManager transactionManager = new JpaTransactionManager(
            nativeEntityManagerFactory);
        return transactionManager;
    }

    @Bean
    @DependsOn("platformTransactionManager")
    @Override
    public SpringTransactionalInterceptor springTransactionalInterceptor() {
        final SpringTransactionalInterceptor springTransactionalInterceptor = new SpringTransactionalInterceptor();
        springTransactionalInterceptor.setTransactionManager(platformTransactionManager());
        return springTransactionalInterceptor;
    }

}
