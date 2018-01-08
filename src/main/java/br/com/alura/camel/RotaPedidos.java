package br.com.alura.camel;

import org.apache.camel.Exchange;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RotaPedidos extends SpringRouteBuilder {

	@Override
	public void configure() throws Exception {
		
		from("file:pedidos?delay=5s&noop=true").
		to("direct:http").
		to("direct:soap");
		
		from("direct:http").
			setProperty("pedidoId", xpath("/pedido/id/text()")).
		    setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()")).
			split().
				xpath("/pedido/itens/item").
			filter().
				xpath("/item/formato[text()='EBOOK']").
			setProperty("ebookId", xpath("/item/livro/codigo/text()")).
			marshal().xmljson().
			log("${id} - ${body}").
//			setHeader(Exchange.FILE_NAME, simple("${file:name.noext}.json")).
			setHeader(Exchange.HTTP_METHOD, HttpMethods.GET).
			setHeader(Exchange.HTTP_QUERY, simple("ebookId=${property.ebookId}&pedidoId=${property.pedidoId}&clienteId=${property.clienteId}")).
		to("http4://localhost:8080/webservices/ebook/item");
		
		from("direct:soap").
		to("mock:soap");
	}
	
}
