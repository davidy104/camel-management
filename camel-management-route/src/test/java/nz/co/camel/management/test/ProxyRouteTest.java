package nz.co.camel.management.test;

import java.io.File;

import nz.co.camel.management.route.ProxyRoute;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyRouteTest extends CamelTestSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyRouteTest.class);

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new ProxyRoute();
	}
	
	@Test
	public void testProcessFile()throws Exception {
		this.template.sendBody("direct:procesFile", null);
	}

//	@Test
//	public void testProcessFile() throws Exception {
//		final Exchange exchange = consumer.receive("file://input?fileName=userinfo-message.txt&delete=false");
//		LOGGER.info("exchange:{} ", exchange);
//		final File file = exchange.getIn().getBody(File.class);
//		LOGGER.info("file:{} ", file);
//	}

}
