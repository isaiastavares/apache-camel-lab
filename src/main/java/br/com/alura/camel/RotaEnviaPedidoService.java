package br.com.alura.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Service;

@Service
public class RotaEnviaPedidoService extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
		from("file:pedidos?noop=true").
		to("activemq:queue:pedidos");
		
	}
	
}
