package com.nimble.server_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ServerSpringApplication {

  public static void main(String[] args) {
    SpringApplication.run(ServerSpringApplication.class, args);
  }

}
