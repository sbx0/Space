package cn.sbx0.space;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpaceApplication{

    public static void main(String[] args) {
        SpringApplication.run(SpaceApplication.class, args);
    }

}
