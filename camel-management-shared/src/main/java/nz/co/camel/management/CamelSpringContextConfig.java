package nz.co.camel.management;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.ManagementStatisticsLevel;
import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.management.DefaultManagementNamingStrategy;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.spring.CamelBeanPostProcessor;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:camel-context-setup.properties")
public class CamelSpringContextConfig {

	@Resource
	private ApplicationContext context;

	@Resource
	private Environment environment;

	private static final String MANAGEMENT_NAMING_HOSTNAME = "managementNamingStrategy.hostname";
	private static final String MANAGEMENT_NAMING_DOMAINNAME = "managementNamingStrategy.domainname";
	private static final String TRACING_ENABLE = "tracing.enable";

	@Bean
	public CamelBeanPostProcessor camelBeanPostProcessor() {
		CamelBeanPostProcessor camelBeanPostProcessor = new CamelBeanPostProcessor();
		camelBeanPostProcessor.setApplicationContext(context);
		return camelBeanPostProcessor;
	}

	@Bean
	public CamelContext camelContext() throws Exception {
		SpringCamelContext camelContext = new SpringCamelContext(context);
		managementStrategySetup(camelContext);
		traceSetup(camelContext);
		camelContext.getExecutorServiceManager().setDefaultThreadPoolProfile(
				genericThreadPoolProfile());
		return camelContext;
	}

	private void managementStrategySetup(CamelContext camelContext) {
		final DefaultManagementNamingStrategy naming = (DefaultManagementNamingStrategy) camelContext.getManagementStrategy().getManagementNamingStrategy();
		naming.setHostName(environment.getRequiredProperty(MANAGEMENT_NAMING_HOSTNAME));
		naming.setDomainName(environment.getRequiredProperty(MANAGEMENT_NAMING_DOMAINNAME));
		camelContext.getManagementStrategy().setStatisticsLevel(ManagementStatisticsLevel.All);
	}

	private void traceSetup(CamelContext camelContext) {
		camelContext.setStreamCaching(true);
		camelContext.setTracing(Boolean.valueOf(environment.getRequiredProperty(TRACING_ENABLE)));
	}

	@Bean
	public ThreadPoolProfile genericThreadPoolProfile() {
		ThreadPoolProfile profile = new ThreadPoolProfile();
		profile.setId("genericThreadPool");
		profile.setKeepAliveTime(120L);
		profile.setPoolSize(2);
		profile.setMaxPoolSize(10);
		profile.setTimeUnit(TimeUnit.SECONDS);
		profile.setRejectedPolicy(ThreadPoolRejectedPolicy.Abort);
		return profile;
	}

}
