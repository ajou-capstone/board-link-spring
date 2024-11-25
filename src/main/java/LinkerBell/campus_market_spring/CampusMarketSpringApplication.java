package LinkerBell.campus_market_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class CampusMarketSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusMarketSpringApplication.class, args);
	}

}
