package nz.co.camel.management.route;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import nz.co.camel.management.model.User;
import nz.co.camel.management.processor.MyManagedBean;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Expression;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ProxyRoute extends RouteBuilder {

	// @PropertyInject(value = "processFileName", defaultValue =
	// "userinfo-message.txt")
	// private String fileName;

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	@Override
	public void configure() throws Exception {
		from("servlet:///users").routeId("servlet:///users").setExchangePattern(ExchangePattern.InOut).bean(MyManagedBean.class, "doSomething").id("myManagedBean").to("direct:procesFile").end();

		from("direct:procesFile").routeId("direct:procesFile").setExchangePattern(ExchangePattern.InOut).process(new Processor() {
			@Override
			public void process(Exchange exchange) throws Exception {
				final ConsumerTemplate consumer = exchange.getContext().createConsumerTemplate();
				final Exchange e = consumer.receive("file://input?fileName=userinfo-message.txt&delete=false");
				exchange.setIn(e.getIn());
			}
		}).to("log:receiveFile?level=INFO&showAll=true&multiline=true").unmarshal().csv().to("log:parsedToCsv?level=INFO&showAll=true&multiline=true").wireTap("direct:outputToQueue")
				.executorServiceRef("genericThreadPool").to("log:continueProcess?level=INFO&showBody=true&multiline=true").transform(new Expression() {
					@SuppressWarnings("unchecked")
					@Override
					public <T> T evaluate(Exchange exchange, Class<T> type) {
						List<User> users = Lists.<User> newArrayList();
						final String bodyString = exchange.getIn().getBody(String.class);
						try {
							Iterable<String> lines = Splitter.on(",").split(bodyString);
							for (Iterator<String> iterator = lines.iterator(); iterator.hasNext();) {
								final String line = iterator.next();
								Iterable<String> values = Splitter.on("||").split(line);
								User user = new User.Builder().userName(Iterables.get(values, 0)).password(Iterables.get(values, 1)).createTime(FORMAT.parse(Iterables.get(values, 2))).build();
								users.add(user);
							}
						} catch (final Exception e) {
							throw new RuntimeException(e);
						}
						return (T) users;
					}
				}).marshal().json(JsonLibrary.Jackson).to("log:jsonBody?level=INFO&showBody=true&multiline=true").end();

		from("direct:outputToQueue").routeId("direct:outputToQueue").split(body()).to("jms:queue:managequeue?jmsMessageType=Text").end();
	}

}
