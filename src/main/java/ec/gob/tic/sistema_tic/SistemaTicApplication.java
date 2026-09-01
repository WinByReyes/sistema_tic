package ec.gob.tic.sistema_tic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity

public class SistemaTicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaTicApplication.class, args);
	}

}
