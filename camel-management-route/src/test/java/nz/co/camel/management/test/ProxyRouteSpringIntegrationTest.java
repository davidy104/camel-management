package nz.co.camel.management.test;

import static org.junit.Assert.fail;

import javax.annotation.Resource;

import nz.co.camel.management.CamelActivemqConfig;
import nz.co.camel.management.CamelSpringContextConfig;
import nz.co.camel.management.config.RouteConfig;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CamelSpringContextConfig.class, CamelActivemqConfig.class, RouteConfig.class })
public class ProxyRouteSpringIntegrationTest {

	@Resource
	private CamelContext camelContext;
	
	@Produce
	private ProducerTemplate producer;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyRouteSpringIntegrationTest.class);

	@After
	public void tearDown() throws Exception {
		final ConsumerTemplate consumer = camelContext.createConsumerTemplate();
		String receivedMessage = "something";
		while (!StringUtils.isEmpty(receivedMessage)){
			receivedMessage = consumer.receiveBody("jms:queue:managequeue", 6000, String.class);
			LOGGER.info("receivedMessage:{} ",receivedMessage);
		}
	}

	@Test
	public void testProcessFile() {
		final String jsonResult = producer.requestBody("direct:procesFile", null, String.class);
		LOGGER.info("get Result:{} ", jsonResult);
	}

}
