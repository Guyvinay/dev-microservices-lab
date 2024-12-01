package com.dev;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DevTakeawayApplication /*implements CommandLineRunner*/ {

//	@Autowired
//	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(DevTakeawayApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		String[] beans = applicationContext.getBeanDefinitionNames();
//		int beansCount = applicationContext.getBeanDefinitionCount();
//		log.info("total beans : {}", beansCount);
//		for (String bean : beans) {
//			log.info("{}", bean);
//		}
//	}

}
