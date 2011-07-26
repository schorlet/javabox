package demo.bc;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SMimeTest {
    private static final Log log = LogFactory.getLog(SMimeTest.class);
    private static final int keySize = 1024;

    private static KeyPair capair = null;
    private static X509Principal caDN = new X509Principal(
        "C=FR, ST=france, L=dijon, O=atol, OU=info, CN=ca");
    private static X509Certificate caCert = null;

    private static PrivateKey aliceKey = null;
    private static X509Certificate aliceCert = null;
    private static X509Principal aliceDN = new X509Principal(
        "C=FR,ST=france,L=dijon,O=atol,OU=info,CN=alice,EMAILADDRESS=alice@atolcd.com");
    private static File alicepemFile = new File("/tmp/alice.pem");
    private static File alicep12File = new File("/tmp/alice.p12");
    private static String alicealias = "alice";

    private static PrivateKey boboKey = null;
    private static X509Certificate boboCert = null;
    private static X509Principal boboDN = new X509Principal(
        "C=FR,ST=france,L=dijon,O=atol,OU=info,CN=bobo,EMAILADDRESS=bobo@atolcd.com");
    private static File bobopemFile = new File("/tmp/bobo.pem");
    private static File bobop12File = new File("/tmp/bobo.p12");
    private static String boboalias = "bobo";

    @BeforeClass
    public static void initializeTestSuite() {
        Security.addProvider(new BouncyCastleProvider());

        alicepemFile.delete();
        alicep12File.delete();
        bobopemFile.delete();
        bobop12File.delete();
    }

    @Test
    public void genCACert() throws Exception {
        log.debug(null);
        log.info("genCACert");

        capair = CertKey.generateKeyPair(keySize);
        Assert.assertNotNull(capair);
        caCert = CertGen.genV3SslCACert(capair, caDN);
        Assert.assertNotNull(caCert);
    }

    @Test
    public void genAliceCert() throws Exception {
        log.debug(null);
        log.info("genAliceCert");

        final KeyPair aliceKeyPair = CertKey.generateKeyPair(keySize);
        aliceKey = aliceKeyPair.getPrivate();
        Assert.assertNotNull(aliceKey);

        aliceCert = CertGen.genV3SigningCert(aliceKeyPair, aliceDN, capair, caDN);
        Assert.assertNotNull(aliceCert);
        CertTools.writePEMObject(aliceCert, alicepemFile);

        CertGen.genP12(alicealias, alicealias, aliceKey, aliceCert, caCert, alicep12File);
        Assert.assertTrue(alicep12File.exists());
    }

    @Test
    public void genBobCert() throws Exception {
        log.debug(null);
        log.info("genBobCert");

        final KeyPair boboKeyPair = CertKey.generateKeyPair(keySize);
        boboKey = boboKeyPair.getPrivate();
        Assert.assertNotNull(boboKey);

        boboCert = CertGen.genV3EncipherCert(boboKeyPair, boboDN, capair, caDN);
        Assert.assertNotNull(boboCert);
        CertTools.writePEMObject(boboCert, bobopemFile);

        CertGen.genP12(boboalias, boboalias, boboKey, boboCert, caCert, bobop12File);
        Assert.assertTrue(bobop12File.exists());
    }

    @Test
    public void aliceSign() throws Exception {
        log.debug(null);
        log.info("aliceSign");

        SMime.createSignedMail(alicep12File, alicealias.toCharArray(), alicealias, null);
        Assert.assertTrue(true);
    }

    @Test
    public void bobCrypt() throws Exception {
        log.debug(null);
        log.info("bobCrypt");

        SMime.createEncryptedMail(aliceCert);
        Assert.assertTrue(true);
    }

    @Test
    public void aliceSignAndCrypt() throws Exception {
        log.debug(null);
        log.info("aliceSignAndCrypt");

        SMime.createSignedAndEncryptedMail(alicep12File, alicealias.toCharArray(), alicealias,
            null, boboCert);
        Assert.assertTrue(true);
    }
}
