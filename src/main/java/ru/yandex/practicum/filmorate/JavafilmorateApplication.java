package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
@SpringBootApplication
public class JavafilmorateApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavafilmorateApplication.class, args);
    }

}
