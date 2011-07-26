package demo.bc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SslClient implements Runnable {
    private static final Log log = LogFactory.getLog(SslClient.class);
    private SSLContext sc = null;

    public static void main(final String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // System.setProperty("javax.net.ssl.keyStore", "/tmp/bali.p12");
        // System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        // System.setProperty("javax.net.ssl.keyStorePassword", "bali");

        final SslClient client = new SslClient();
        client.run();
    }

    public SslClient() throws Exception {
        final File keyStore = new File(System.getProperty("javax.net.ssl.keyStore"));
        final String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
        final String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");

        File trustStore = new File(System.getProperty("javax.net.ssl.trustStore"));
        String trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        if (trustStore == null) {
            trustStore = keyStore;
            trustStoreType = keyStoreType;
            trustStorePassword = keyStorePassword;
        }

        final KeyStore ks = KeyStore.getInstance(keyStoreType, "BC");
        ks.load(new FileInputStream(keyStore), keyStorePassword.toCharArray());

        final KeyStore ts = KeyStore.getInstance(trustStoreType, "BC");
        ts.load(new FileInputStream(trustStore), trustStorePassword.toCharArray());

        final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, null);

        final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ts);

        sc = SSLContext.getInstance("TLS");
        sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }

    public void run() {
        final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            final SSLSocketFactory f = sc.getSocketFactory();
            final SSLSocket c = (SSLSocket) f.createSocket("localhost", 8443);
            if (log.isDebugEnabled()) {
                printSocketInfo(c);
            }

            c.startHandshake();
            final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
            final BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String m = null;
            while ((m = r.readLine()) != null) {
                System.out.println(m);
                m = in.readLine();
                w.write(m, 0, m.length());
                w.newLine();
                w.flush();
            }
            w.close();
            r.close();
            c.close();
        } catch (final Exception e) {
            log.error(e);
        }
    }

    private static void printSocketInfo(final SSLSocket s) {
        log.debug("Socket class: " + s.getClass());
        log.debug("   Remote address = " + s.getInetAddress().toString());
        log.debug("   Remote port = " + s.getPort());
        log.debug("   Local socket address = " + s.getLocalSocketAddress().toString());
        log.debug("   Local address = " + s.getLocalAddress().toString());
        log.debug("   Local port = " + s.getLocalPort());
        log.debug("   Need client authentication = " + s.getNeedClientAuth());
        final SSLSession ss = s.getSession();
        try {
            log.debug("Session class: " + ss.getClass());
            log.debug("   Cipher suite = " + ss.getCipherSuite());
            log.debug("   Protocol = " + ss.getProtocol());
            log.debug("   PeerPrincipal = " + ss.getPeerPrincipal().getName());
            log.debug("   LocalPrincipal = " + ss.getLocalPrincipal().getName());
        } catch (final Exception e) {
            log.error(e);
        }
    }
}
