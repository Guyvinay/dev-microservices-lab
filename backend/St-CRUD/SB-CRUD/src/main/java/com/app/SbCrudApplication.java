package com.app;

import com.app.configuration.PrivilegeAuth;
import com.app.configuration.ProducerConfig;
import com.app.service.DataService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
//@RestController
@EnableCaching
public class SbCrudApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(SbCrudApplication.class, args);

					// ProducerConfig producerConfig = context.getBean(ProducerConfig.class);
					// producerConfig.sendMessage("Message");;
	}



	//	@GetMapping(value = "/h")
//	public String sayHello(){
//		return "Hello Wolf";
//	}
}
