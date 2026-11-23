package org.AnsySan.config;

import org.AnsySan.account.Account;
import org.AnsySan.user.User;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class HibernateConfiguration {
    @Bean(destroyMethod = "close")
    public SessionFactory sessionFactory(Environment environment) {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.addAnnotatedClass(User.class)
                .addAnnotatedClass(Account.class)
                .setProperty("hibernate.connection.driver_class", environment.getRequiredProperty("db.driver"))
                .setProperty("hibernate.connection.url", environment.getRequiredProperty("db.url"))
                .setProperty("hibernate.connection.username", environment.getRequiredProperty("db.username"))
                .setProperty("hibernate.connection.password", environment.getRequiredProperty("db.password"))
                .setProperty("hibernate.dialect", environment.getRequiredProperty("db.dialect"))
                .setProperty("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"))
                .setProperty("hibernate.current_session_context_class", environment.getRequiredProperty("hibernate.current_session_context_class"))
                .setProperty("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"))
                .setProperty("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        return configuration.buildSessionFactory();
    }
}
