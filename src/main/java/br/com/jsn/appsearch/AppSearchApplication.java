package br.com.jsn.appsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "br.com.jsn")
public class AppSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppSearchApplication.class, args);
	}

}
