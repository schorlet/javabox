package demo.bc;

import java.io.File;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CertGenTest {
    private static final Log log = LogFactory.getLog(CertGenTest.class);
    private static final int keySize = 2048;

    private static File privateCAKeyFile = new File("/tmp/ca.key");
    private static File publicCAKeyFile = new File("/tmp/ca.pub");
    private static File privateServerKeyFile = new File("/tmp/localhost.key");
    private static File publicServerKeyFile = new File("/tmp/localhost.pub");
    private static File privateClientKeyFile = new File("/tmp/bali.key");
    private static File publicClientKeyFile = new File("/tmp/bali.pub");
    private static File privateMailKeyFile = new File("/tmp/mail.key");
    private static File publicMailKeyFile = new File("/tmp/mail.pub");

    private static File caCertPemFile = new File("/tmp/ca.pem");
    private static File caCertp12File = new File("/tmp/ca.p12");
    private static File serverPemFile = new File("/tmp/localhost.pem");
    private static File serverp12File = new File("/tmp/localhost.p12");
    private static File serverjksFile = new File("/tmp/localhost.jks");
    private static File clientPemFile = new File("/tmp/bali.pem");
    private static File clientp12File = new File("/tmp/bali.p12");
    private static File clientjksFile = new File("/tmp/bali.jks");
    private static File mailPemFile = new File("/tmp/mail.pem");
    private static File mailp12File = new File("/tmp/mail.p12");

    private static final String caAlias = "rootca";
    private static final String serverAlias = "localhost";
    private static final String clientAlias = "bali";
    private static final String mailAlias = "mail";

    private static X509Principal caDN = new X509Principal(
        "C=FR, ST=france, L=nantes, O=washingmaching, OU=info, CN=ca");
    private static X509Principal serverDN = new X509Principal(
        "C=FR, ST=france, L=nantes, O=washingmaching, OU=info, CN=localhost");
    private static X509Principal clientDN = new X509Principal(
        "C=FR, ST=france, L=nantes, O=washingmaching, OU=info, CN=bali, E=bali@washingmaching");
    private static X509Principal mailDN = new X509Principal(
        "C=FR, ST=france, L=nantes, O=washingmaching, OU=info, CN=toto, E=toto@washingmaching");

    private static KeyPair privateCAKey = null;
    private static PublicKey publicCAKey = null;
    private static KeyPair privateServerKey = null;
    private static PublicKey publicServerKey = null;
    private static KeyPair privateClientKey = null;
    private static PublicKey publicClientKey = null;
    private static KeyPair privateMailKey = null;
    private static PublicKey publicMailKey = null;

    private static X509Certificate caCert = null;
    private static X509Certificate serverCert = null;
    private static X509Certificate clientCert = null;
    private static X509Certificate mailCert = null;

    @BeforeClass
    public static void initializeTestSuite() {
        Security.addProvider(new BouncyCastleProvider());

        privateCAKeyFile.delete();
        publicCAKeyFile.delete();

        privateServerKeyFile.delete();
        publicServerKeyFile.delete();

        privateClientKeyFile.delete();
        publicClientKeyFile.delete();

        privateMailKeyFile.delete();
        publicMailKeyFile.delete();

        caCertPemFile.delete();
        serverPemFile.delete();
        clientPemFile.delete();
        mailPemFile.delete();

        caCertp12File.delete();
        serverp12File.delete();
        clientp12File.delete();
        mailp12File.delete();

        serverjksFile.delete();
        clientjksFile.delete();
    }

    @AfterClass
    public static void finalizeTestSuite() {}

    @Test
    public void writeCAKeyPair() throws Exception {
        log.debug(null);
        log.info("writeCAKeyPair");

        final KeyPair pair = CertKey.generateKeyPair(keySize);
        CertKey.writeKeyPair(pair, privateCAKeyFile, publicCAKeyFile);
        Assert.assertTrue(privateCAKeyFile.exists());
        Assert.assertTrue(publicCAKeyFile.exists());
    }

    @Test
    public void writeServerKeyPair() throws Exception {
        log.debug(null);
        log.info("writeServerKeyPair");

        final KeyPair pair = CertKey.generateKeyPair(keySize);
        CertKey.writeKeyPair(pair, privateServerKeyFile, publicServerKeyFile);
        Assert.assertTrue(privateServerKeyFile.exists());
        Assert.assertTrue(publicServerKeyFile.exists());
    }

    @Test
    public void writeClientKeyPair() throws Exception {
        log.debug(null);
        log.info("writeClientKeyPair");

        final KeyPair pair = CertKey.generateKeyPair(keySize);
        CertKey.writeKeyPair(pair, privateClientKeyFile, publicClientKeyFile);
        Assert.assertTrue(privateClientKeyFile.exists());
        Assert.assertTrue(publicClientKeyFile.exists());
    }

    @Test
    public void writeMailKeyPair() throws Exception {
        log.debug(null);
        log.info("writeMailKeyPair");

        final KeyPair pair = CertKey.generateKeyPair(keySize);
        CertKey.writeKeyPair(pair, privateMailKeyFile, publicMailKeyFile);
        Assert.assertTrue(privateMailKeyFile.exists());
        Assert.assertTrue(publicMailKeyFile.exists());
    }

    @Test
    public void readCAKeyPair() throws Exception {
        log.debug(null);
        log.info("readCAKeyPair");

        privateCAKey = CertKey.readKeyPair(privateCAKeyFile);
        publicCAKey = CertKey.readPublicKey(publicCAKeyFile);
        Assert.assertNotNull(privateCAKey);
        Assert.assertNotNull(publicCAKey);
    }

    @Test
    public void readServerKeyPair() throws Exception {
        log.debug(null);
        log.info("readServerKeyPair");

        privateServerKey = CertKey.readKeyPair(privateServerKeyFile);
        publicServerKey = CertKey.readPublicKey(publicServerKeyFile);
        Assert.assertNotNull(privateServerKey);
        Assert.assertNotNull(publicServerKey);
    }

    @Test
    public void readClientKeyPair() throws Exception {
        log.debug(null);
        log.info("readClientKeyPair");

        privateClientKey = CertKey.readKeyPair(privateClientKeyFile);
        publicClientKey = CertKey.readPublicKey(publicClientKeyFile);
        Assert.assertNotNull(privateClientKey);
        Assert.assertNotNull(publicClientKey);
    }

    @Test
    public void readMailKeyPair() throws Exception {
        log.debug(null);
        log.info("readMailKeyPair");

        privateMailKey = CertKey.readKeyPair(privateMailKeyFile);
        publicMailKey = CertKey.readPublicKey(publicMailKeyFile);
        Assert.assertNotNull(privateMailKey);
        Assert.assertNotNull(publicMailKey);
    }

    @Test
    public void genSslCA() throws Exception {
        log.debug(null);
        log.info("genSslCA");

        caCert = CertGen.genV3SslCACert(privateCAKey, caDN);
        Assert.assertNotNull(caCert);
        CertTools.writePEMObject(caCert, caCertPemFile);
        Assert.assertTrue(caCertPemFile.exists());
    }

    @Test
    public void genSslServer() throws Exception {
        log.debug(null);
        log.info("genSslServer");

        serverCert = CertGen.genV3SslServerCert(privateServerKey, serverDN, privateCAKey, caDN);
        Assert.assertNotNull(serverCert);
        CertTools.writePEMObject(serverCert, serverPemFile);
        Assert.assertTrue(serverPemFile.exists());
    }

    @Test
    public void genSslClient() throws Exception {
        log.debug(null);
        log.info("genSslClient");

        clientCert = CertGen.genV3SslClientCert(privateClientKey, clientDN, privateCAKey, caDN);
        Assert.assertNotNull(clientCert);
        CertTools.writePEMObject(clientCert, clientPemFile);
        Assert.assertTrue(clientPemFile.exists());
    }

    @Test
    public void genSslMail() throws Exception {
        log.debug(null);
        log.info("genSslMail");

        mailCert = CertGen.genV3SigningCert(privateMailKey, mailDN, privateCAKey, caDN);
        Assert.assertNotNull(mailCert);
        CertTools.writePEMObject(mailCert, mailPemFile);
        Assert.assertTrue(mailPemFile.exists());
    }

    @Test
    public void genCaP12() throws Exception {
        log.debug(null);
        log.info("genCaP12");
        CertGen.genP12(caAlias, caAlias, privateCAKey.getPrivate(), caCert, caCert, caCertp12File);
        Assert.assertTrue(caCertp12File.exists());
    }

    @Test
    public void genServerP12() throws Exception {
        log.debug(null);
        log.info("genServerP12");
        CertGen.genP12(serverAlias, serverAlias, privateServerKey.getPrivate(), serverCert, caCert,
            serverp12File);
        Assert.assertTrue(serverp12File.exists());
    }

    @Test
    public void genClientP12() throws Exception {
        log.debug(null);
        log.info("genClientP12");
        CertGen.genP12(clientAlias, clientAlias, privateClientKey.getPrivate(), clientCert, caCert,
            clientp12File);
        Assert.assertTrue(clientp12File.exists());
    }

    @Test
    public void genMailP12() throws Exception {
        log.debug(null);
        log.info("genMailP12");
        CertGen.genP12(mailAlias, mailAlias, privateMailKey.getPrivate(), mailCert, caCert,
            mailp12File);
        Assert.assertTrue(mailp12File.exists());
    }

    @Test
    public void genServerJKS() throws Exception {
        log.debug(null);
        log.info("genServerJKS");
        CertGen.genJKS(serverAlias, serverAlias, privateServerKey.getPrivate(), serverCert, caCert,
            serverjksFile);
        Assert.assertTrue(serverjksFile.exists());
    }

    @Test
    public void genClientJKS() throws Exception {
        log.debug(null);
        log.info("genClientJKS");
        CertGen.genJKS(clientAlias, clientAlias, privateClientKey.getPrivate(), clientCert, caCert,
            clientjksFile);
        Assert.assertTrue(clientjksFile.exists());
    }
}
