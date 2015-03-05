package nz.co.camel.management.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import nz.co.camel.management.route.ProxyRoute;

import org.apache.camel.CamelContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

	@Resource
	private CamelContext camelContext;

	@PostConstruct
	public void registerRoutes() throws Exception {
		camelContext.addRoutes(new ProxyRoute());
	}

}
