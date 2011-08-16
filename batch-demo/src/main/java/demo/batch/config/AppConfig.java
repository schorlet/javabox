package demo.batch.config;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ DataConfigJPA.class })
public class AppConfig {

    @Bean
    BeanNameAutoProxyCreator autoProxyCreator() {
        final BeanNameAutoProxyCreator proxyCreator = new BeanNameAutoProxyCreator();

        proxyCreator.setInterceptorNames(new String[] { "transactionInterceptor" });
        proxyCreator.setBeanNames(new String[] { "customerRepository", "orderRepository",
            "productRepository" });
        return proxyCreator;
    }

}
