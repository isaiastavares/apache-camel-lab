package br.com.alura.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.springframework.stereotype.Service;

@Service
public class RotaPedidoService extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		
//		errorHandler(deadLetterChannel("file:erro").
		errorHandler(deadLetterChannel("activemq:queue:pedidos.DLQ").
				logExhausted(true).
					maximumRedeliveries(3).
						redeliveryDelay(2000).
							onRedelivery(new Processor() {
								@Override
								public void process(Exchange exchange) throws Exception {
									int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
									int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
									System.out.println("Redelivery " + counter + "/" + max);
								}
							}));
		
//		from("file:pedidos?delay=5s&noop=true").
		from("activemq:queue:pedidos").
			routeId("rota-pedidos").
			to("validator:pedido.xsd").
			multicast().
				to("direct:http").
				to("direct:soap");
		
		from("direct:http").
			routeId("rota-http").
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
			routeId("rota-soap").
			to("xslt:pedido-para-soap.xslt").
			log("${body}").
			setHeader(Exchange.CONTENT_TYPE, constant("text/xml")).
		to("http4://localhost:8080/webservices/financeiro");
	}
	
}
