package br.com.alura.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RotaPedidos extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("file:pedidos?delay=5s&noop=true")
			.log("${id}")
			.marshal().xmljson()
			.log("${body}")
			.setHeader("CamelFileName", simple("${file:name.noext}.json"))
		.to("file:saida");
	}
}
