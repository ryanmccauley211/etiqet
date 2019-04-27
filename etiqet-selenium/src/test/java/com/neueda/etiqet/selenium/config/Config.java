package com.neueda.etiqet.selenium.config;

import com.neueda.etiqet.selenium.browser.BrowserBase;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;

public class Config {

    private static Logger logger = Logger.getLogger(BrowserBase.class);

    public static void init() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL configUrl = classLoader.getResource("config/browsers.xml");
        if (configUrl == null) {
            return;
        }
        try {
            logger.info("Initializing config...");
            File file = new File(configUrl.toURI());
            JAXBContext jaxbContext = JAXBContext.newInstance(BrowserBase.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.unmarshal(file);
        } catch (URISyntaxException e) {
            logger.error("Unable to find config file with path " + configUrl);
            e.printStackTrace();
        } catch (JAXBException e) {
            logger.error("Failed to unmarshall BrowserBase to setup browser configs");
            e.printStackTrace();
        }
    }
}
