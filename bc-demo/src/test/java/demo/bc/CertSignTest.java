package demo.bc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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

public class CertSignTest {
    private static final Log log = LogFactory.getLog(CertSignTest.class);
    private static final int keySize = 2048;

    private static PrivateKey aliceKey = null;
    private static X509Certificate aliceCert = null;
    private static X509Principal aliceDN = new X509Principal(
        "C=FR, ST=france, L=dijon, O=atol, OU=info, CN=alice, E=alice@atolcd.com");

    private static PrivateKey bobKey = null;
    private static X509Certificate bobCert = null;
    private static X509Principal bobDN = new X509Principal(
        "C=FR, ST=france, L=dijon, O=atol, OU=info, CN=bob, E=bob@atolcd.com");

    private static File clearFile = new File("/tmp/clear.txt");
    private static File cipherFile = new File("/tmp/cipher.txt");
    private static File signatureFile = new File("/tmp/signature.txt");

    private static String clearString = "abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789";

    @BeforeClass
    public static void initializeTestSuite() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void genAliceCert() throws Exception {
        log.debug(null);
        log.info("genAliceCert");

        final KeyPair aliceKeyPair = CertKey.generateKeyPair(keySize);
        aliceKey = aliceKeyPair.getPrivate();
        Assert.assertNotNull(aliceKey);

        aliceCert = CertGen.genV3SigningCert(aliceKeyPair, aliceDN, aliceKeyPair, aliceDN);
        Assert.assertNotNull(aliceCert);
        Assert.assertNotNull(aliceCert.getPublicKey());
    }

    @Test
    public void genBobCert() throws Exception {
        log.debug(null);
        log.info("genBobCert");

        final KeyPair bobKeyPair = CertKey.generateKeyPair(keySize);
        bobKey = bobKeyPair.getPrivate();
        Assert.assertNotNull(bobKey);

        bobCert = CertGen.genV3EncipherCert(bobKeyPair, bobDN, bobKeyPair, bobDN);
        Assert.assertNotNull(bobCert);
        Assert.assertNotNull(bobCert.getPublicKey());
    }

    @Test
    public void aliceSignAndCrypt() throws Exception {
        log.debug(null);
        log.info("aliceSignAndCrypt");

        writeClearFile();

        // sign
        final byte[] digest = CertSign.createDigest(clearFile);
        final byte[] signature = CertSign.createSignature(aliceKey, digest);
        writeSignatureFile(signature);
        Assert.assertTrue(signatureFile.exists());

        // crypt
        final Crypto crypto = new Crypto(bobCert.getPublicKey());
        final FileInputStream in = new FileInputStream(clearFile);
        final FileOutputStream out = new FileOutputStream(cipherFile);
        crypto.encrypt(in, out);
        Assert.assertTrue(cipherFile.exists());
    }

    @Test
    public void bobDecryptAndVerify() throws Exception {
        log.debug(null);
        log.info("bobDecryptAndVerify");

        // decrypt
        final Crypto crypto = new Crypto(bobKey);
        final FileInputStream in = new FileInputStream(cipherFile);
        final FileOutputStream out = new FileOutputStream(clearFile);
        crypto.decrypt(in, out);
        final char cbuf[] = readClearFile();
        Assert.assertTrue(clearString.equals(String.valueOf(cbuf)));

        // verify
        final byte[] digest = CertSign.createDigest(clearFile);
        final byte[] signature = readSignatureFile();
        final boolean isSignedByAlice = CertSign.verifySignature(aliceCert.getPublicKey(), digest,
            signature);
        Assert.assertTrue(isSignedByAlice);
    }

    // /////////////////////////////////////////////////////////////////

    static void writeClearFile() throws Exception {
        final FileWriter fw = new FileWriter(clearFile);
        fw.write(clearString);
        fw.close();
    }

    static char[] readClearFile() throws Exception {
        final FileReader in = new FileReader(clearFile);
        final char[] bbuf = new char[(int) clearFile.length()];
        in.read(bbuf);
        in.close();
        return bbuf;
    }

    static void writeSignatureFile(final byte[] signature) throws Exception {
        final FileOutputStream out = new FileOutputStream(signatureFile);
        out.write(signature);
        out.close();
    }

    static byte[] readSignatureFile() throws Exception {
        final FileInputStream in = new FileInputStream(signatureFile);
        final byte[] bbuf = new byte[(int) signatureFile.length()];
        in.read(bbuf);
        in.close();
        return bbuf;
    }
}
