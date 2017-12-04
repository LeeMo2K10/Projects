
package example;

import example.model.Order;

import org.apache.log4j.Logger;
import org.milyn.Smooks;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaSource;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamResult;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

public class Main {
	public static Logger logger = Logger.getLogger(Main.class);

	protected String runSmooksTransform(Object inputJavaObject) {
		Smooks smooks = null;
		try {
			smooks = new Smooks("src/main/resources/smooks-config.xml");
		} catch (IOException e) {

			logger.error(e.getMessage());
		} catch (SAXException e) {

			logger.error(e.getMessage());
		}

		try {
			ExecutionContext executionContext = smooks.createExecutionContext();
			StringWriter writer = new StringWriter();
			FileOutputStream fout = null;
			try {
				fout = new FileOutputStream("src/main/resources/output.xml");
			} catch (FileNotFoundException e) {

				logger.error(e.getMessage());
			}
			smooks.filterSource(executionContext, new JavaSource(inputJavaObject), new StreamResult(fout));

			return writer.toString();
		} finally {
			smooks.close();
		}
	}

	public static void main(String[] args) {
		Main smooksMain = new Main();
		Order javaInput = new Order();
		String transResult = null;

		logger.debug(javaInput);

		logger.info("This needs to be transformed to XML.");

		transResult = smooksMain.runSmooksTransform(javaInput);

		logger.debug(transResult);
		logger.info("Check output in output.xml");

	}

}