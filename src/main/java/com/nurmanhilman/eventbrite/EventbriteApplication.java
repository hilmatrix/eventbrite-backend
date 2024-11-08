package com.nurmanhilman.eventbrite;

import com.nurmanhilman.eventbrite.config.RsaKeyConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties({RsaKeyConfigProperties.class})
public class EventbriteApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventbriteApplication.class, args);

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		System.out.println("Password Hash: " + passwordEncoder.encode("12345678"));
	}

}
