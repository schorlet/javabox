package demo;

import org.apache.log4j.Logger;

public class DemoLog {
    static Logger logger = Logger.getLogger("demo");

    public static void main(final String[] args) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            logger.debug("debug");
            logger.info("info");
            logger.warn("warn");
            logger.error("error");

            Thread.sleep(1000);
        }
    }
}
