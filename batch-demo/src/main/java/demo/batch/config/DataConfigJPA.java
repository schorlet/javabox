package demo.batch.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
public class DataConfigJPA {

    @Bean
    JpaVendorAdapter jpaVendorAdapter() {
        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        // jpaVendorAdapter.setShowSql(false);
        jpaVendorAdapter.setGenerateDdl(false);
        return jpaVendorAdapter;
    }

    @Bean
    @DependsOn("jpaVendorAdapter")
    AbstractEntityManagerFactoryBean dataStore() {
        final LocalEntityManagerFactoryBean entityManagerFactory = new LocalEntityManagerFactoryBean();
        entityManagerFactory.setPersistenceUnitName("dataStore");
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
        return entityManagerFactory;
    }

    @Bean
    @DependsOn("dataStore")
    PlatformTransactionManager transactionManager() {
        final EntityManagerFactory nativeEntityManagerFactory = dataStore().getObject();
        final JpaTransactionManager transactionManager = new JpaTransactionManager(
            nativeEntityManagerFactory);
        return transactionManager;
    }

    @Bean
    @DependsOn("transactionManager")
    TransactionInterceptor transactionInterceptor() {
        final TransactionInterceptor transactionInterceptor = new TransactionInterceptor(
            transactionManager(), new AnnotationTransactionAttributeSource());

        return transactionInterceptor;
    }

}
