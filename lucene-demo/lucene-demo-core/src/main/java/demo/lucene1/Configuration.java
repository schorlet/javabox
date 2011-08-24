package demo.lucene1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads properties files from classpath, user.home and user.dir if exists.
 * <p>
 * reads classpath/lucene-demo.properties<br/>
 * reads user.home/.lucene-demo.properties<br/>
 * reads user.dir/.lucene-demo.properties<br/>
 * </p>
 *
 * Properties found in user.dir overrides properties from user.home which overrides classpath properties.
 *
 * @author sch
 */
final class Configuration {
    final Logger logger = LoggerFactory.getLogger(getClass());

    final Properties cp_props = new Properties();
    final Properties home_props = new Properties(cp_props);
    final Properties exec_props = new Properties(home_props);
    final Properties all_props = new Properties(exec_props);

    Configuration() {
        // classpath
        final URL classpathURL = Configuration.class.getResource("/lucene-demo.properties");
        loadURL(cp_props, classpathURL);

        // user.home
        final String userPath = System.getProperty("user.home");
        final File userFile = new File(userPath, "/.lucene-demo.properties");
        loadFile(home_props, userFile);

        // user.dir
        final String executionPath = System.getProperty("user.dir");
        final File executionFile = new File(executionPath, "/.lucene-demo.properties");
        loadFile(exec_props, executionFile);
    }

    String get(final String key) {
        return all_props.getProperty(key);
    }

    void loadFile(final Properties properties, final File file) {
        if (file != null && file.exists() && file.isFile() && file.canRead()) {
            try {
                loadURL(properties, file.toURI().toURL());
            } catch (final MalformedURLException e) {
                logger.error("unable to read: " + file.toString(), e);
            }
        }
    }

    void loadURL(final Properties properties, final URL url) {
        InputStream stream = null;
        try {
            stream = url.openStream();
            properties.load(stream);
            logger.info("read properties from: {}", url.toString());

        } catch (final IOException e) {
            logger.error("unable to read: " + url.toString(), e);

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (final IOException e) {
                    stream = null;
                }
            }
        }
    }
}
