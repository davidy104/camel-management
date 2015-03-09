package nz.co.camel.management;

import nz.co.camel.management.config.RouteConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
// @ComponentScan(basePackages = "nz.co.camel.management")
@Import(value = { CamelSpringContextConfig.class, CamelActivemqConfig.class,RouteConfig.class })
public class ApplicationConfiguration {
}
