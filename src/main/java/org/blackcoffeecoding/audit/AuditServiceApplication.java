package org.blackcoffeecoding.audit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// Отключаем автоконфигурацию БД, так как у нас её пока нет (мы пишем только в логи)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AuditServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditServiceApplication.class, args);
    }

}