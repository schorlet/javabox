package org.apache.log4j.chainsaw;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class LoggingClientReceiver extends Thread {
    /** used to log messages **/
    private static final Logger LOG = Logger.getLogger(LoggingReceiver.class);

    /** where to put the events **/
    private final MyTableModel mModel;

    /** socket connection to read events from **/
    final Socket mClient;

    LoggingClientReceiver(final MyTableModel aModel, final String aHost, final int aPort)
        throws IOException {
        setDaemon(true);
        mModel = aModel;
        mClient = new Socket(aHost, aPort);
    }

    /** loops getting the events **/
    @Override
    public void run() {
        LOG.debug("Starting to get data");
        try {
            final ObjectInputStream ois = new ObjectInputStream(mClient.getInputStream());
            while (true) {
                final LoggingEvent event = (LoggingEvent) ois.readObject();
                mModel.addEvent(new EventDetails(event));
            }
        } catch (final EOFException e) {
            LOG.info("Reached EOF, closing connection");
        } catch (final SocketException e) {
            LOG.info("Caught SocketException, closing connection");
        } catch (final IOException e) {
            LOG.warn("Got IOException, closing connection", e);
        } catch (final ClassNotFoundException e) {
            LOG.warn("Got ClassNotFoundException, closing connection", e);
        }

        try {
            mClient.close();
        } catch (final IOException e) {
            LOG.warn("Error closing connection", e);
        }
    }

}
