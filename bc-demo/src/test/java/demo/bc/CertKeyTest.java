package demo.bc;

import java.io.File;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CertKeyTest {
    private static final Log log = LogFactory.getLog(CertKeyTest.class);
    private static final int keySize = 2048;

    private static File privateClientKeyFile = new File("/tmp/bali.key");
    private static File publicClientKeyFile = new File("/tmp/bali.pub");

    private static KeyPair privateClientKey = null;
    private static PublicKey publicClientKey = null;
    private static final String clientPasswd = "bali";

    @BeforeClass
    public static void initializeTestSuite() {
        Security.addProvider(new BouncyCastleProvider());

        privateClientKeyFile.delete();
        publicClientKeyFile.delete();
    }

    @Test
    public void writeClientKeyPair() throws Exception {
        log.debug(null);
        log.info("writeClientKeyPair");

        final KeyPair pair = CertKey.generateKeyPair(keySize);
        CertKey.writeKeyPair(pair, privateClientKeyFile, publicClientKeyFile, clientPasswd);
        Assert.assertTrue(privateClientKeyFile.exists());
        Assert.assertTrue(publicClientKeyFile.exists());
    }

    @Test
    public void readClientKeyPair() throws Exception {
        log.debug(null);
        log.info("readClientKeyPair");

        privateClientKey = CertKey.readKeyPair(privateClientKeyFile, clientPasswd);
        publicClientKey = CertKey.readPublicKey(publicClientKeyFile);
        Assert.assertNotNull(privateClientKey);
        Assert.assertNotNull(publicClientKey);
    }

}
