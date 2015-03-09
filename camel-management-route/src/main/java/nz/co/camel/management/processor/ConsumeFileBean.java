package nz.co.camel.management.processor;

import java.io.File;
import java.util.List;

import org.apache.camel.ConsumerTemplate;

public class ConsumeFileBean {

	private ConsumerTemplate consumer;
	
	public List<String> readLinesFromFile(final File file)throws Exception {
		consumer.receive("");
		return null;
	}
}
