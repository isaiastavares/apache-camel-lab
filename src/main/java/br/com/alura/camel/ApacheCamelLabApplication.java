package br.com.alura.camel;

import javax.annotation.PostConstruct;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApacheCamelLabApplication {
	
	@Autowired
    private CamelContext context;

    @PostConstruct
    public void init() {
        context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));
    }

	public static void main(String[] args) {
		SpringApplication.run(ApacheCamelLabApplication.class, args);
	}
	
}
