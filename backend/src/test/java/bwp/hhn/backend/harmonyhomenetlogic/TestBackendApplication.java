package bwp.hhn.backend.harmonyhomenetlogic;

import bwp.hhn.backend.harmonyhomenetlogic.config.TestContainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(BackendApplication::main).with(TestContainersConfiguration.class).run(args);
	}
}