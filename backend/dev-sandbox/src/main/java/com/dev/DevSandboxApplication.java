package com.dev;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevSandboxApplication /*implements CommandLineRunner*/ {

//	@Autowired
//	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(DevSandboxApplication.class, args);
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
