package shopy.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShofiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShofiApplication.class, args);
		System.out.println("Order Service is running....");
	}

}
