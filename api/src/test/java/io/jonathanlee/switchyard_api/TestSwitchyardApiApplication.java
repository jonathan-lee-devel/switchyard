package io.jonathanlee.switchyard_api;

import org.springframework.boot.SpringApplication;

public class TestSwitchyardApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(SwitchyardApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
