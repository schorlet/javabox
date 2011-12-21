/*
 * Copyright 1999-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.log4j.chainsaw;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * The main application.
 *
 * @author <a href="mailto:oliver@puppycrawl.com">Oliver Burn</a>
 */
public class Chainsaw extends JFrame {
    private static final long serialVersionUID = 1591325077855592990L;

    private static final int DEFAULT_PORT = 4445;

    /** name of property for port name **/
    public static final String PORT_PROP_NAME = "chainsaw.port";

    /** name of property for port name **/
    public static final String HOST_PROP_NAME = "chainsaw.host";

    /** use to log messages **/
    private final Logger logger = Logger.getLogger(Chainsaw.class);

    /**
     * Creates a new <code>Chainsaw</code> instance.
     */
    private Chainsaw() {
        super("CHAINSAW - Log4J Log Viewer");
        // create the all important model
        final MyTableModel model = new MyTableModel();

        // Create the menu bar.
        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        final JMenu menu = new JMenu("File");
        menuBar.add(menu);

        try {
            final LoadXMLAction lxa = new LoadXMLAction(this, model);
            final JMenuItem loadMenuItem = new JMenuItem("Load file...");
            menu.add(loadMenuItem);
            loadMenuItem.addActionListener(lxa);
        } catch (final NoClassDefFoundError e) {
            logger.info("Missing classes for XML parser", e);
            JOptionPane.showMessageDialog(this,
                "XML parser not in classpath - unable to load XML events.", "CHAINSAW",
                JOptionPane.ERROR_MESSAGE);
        } catch (final Exception e) {
            logger.info("Unable to create the action to load XML files", e);
            JOptionPane.showMessageDialog(this,
                "Unable to create a XML parser - unable to load XML events.", "CHAINSAW",
                JOptionPane.ERROR_MESSAGE);
        }

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        menu.add(exitMenuItem);
        exitMenuItem.addActionListener(ExitAction.INSTANCE);

        // Add control panel
        final ControlPanel cp = new ControlPanel(model);
        getContentPane().add(cp, BorderLayout.NORTH);

        // Create the table
        final JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Events: "));
        scrollPane.setPreferredSize(new Dimension(900, 300));

        // Create the details
        final JPanel details = new DetailPanel(table, model);
        details.setPreferredSize(new Dimension(900, 300));

        // Add the table and stack trace into a splitter
        final JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, details);
        getContentPane().add(jsp, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent aEvent) {
                ExitAction.INSTANCE.actionPerformed(null);
            }
        });

        pack();
        setVisible(true);

        setupReceiver(model);
    }

    /**
     * Setup recieving messages.
     */
    private void setupReceiver(final MyTableModel aModel) {
        int port = DEFAULT_PORT;
        final String strRep = System.getProperty(PORT_PROP_NAME);
        if (strRep != null) {
            try {
                port = Integer.parseInt(strRep);
            } catch (final NumberFormatException nfe) {
                logger.fatal("Unable to parse " + PORT_PROP_NAME + " property with value " + strRep
                    + ".");
                JOptionPane.showMessageDialog(this, "Unable to parse port number from '" + strRep
                    + "', quitting.", "CHAINSAW", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        final String strHost = System.getProperty(HOST_PROP_NAME);
        if (strHost == null) {
            logger.fatal(HOST_PROP_NAME + " is undefined");
            JOptionPane.showMessageDialog(this, HOST_PROP_NAME + " is undefined, quitting.",
                "CHAINSAW", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            final LoggingClientReceiver lr = new LoggingClientReceiver(aModel, strHost, port);
            lr.start();

        } catch (final IOException e) {
            logger.fatal("Unable to connect socket on " + strHost + ":" + port + ", quitting.", e);
            JOptionPane.showMessageDialog(this, "Unable to connect socket on " + strHost + ":"
                + port + ", quitting.", "CHAINSAW", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // //////////////////////////////////////////////////////////////////////////
    // static methods
    // //////////////////////////////////////////////////////////////////////////

    /** initialise log4j **/
    private static void initLog4J() {
        final Properties props = new Properties();
        props.setProperty("log4j.rootLogger", "DEBUG, A1");
        props.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
        props.setProperty("log4j.appender.A1.layout", "org.apache.log4j.TTCCLayout");

        PropertyConfigurator.configure(props);
    }

    public static void main(final String[] aArgs) {
        initLog4J();
        new Chainsaw();
    }
}
