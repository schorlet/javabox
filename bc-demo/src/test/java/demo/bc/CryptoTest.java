package demo.bc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CryptoTest {
    private static final Log log = LogFactory.getLog(CryptoTest.class);

    private static File clearFile = new File("/tmp/clear.txt");
    private static File cipherFile = new File("/tmp/cipher.txt");
    private static String clearString = "abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789";

    static byte[] salt = new byte[8];
    static boolean base64 = true;

    private static String des_ede3_cbc_string = "U2FsdGVkX1+SEE8M34iu9QO3dJmlYx0afEPkVMWGDmkZeQ4zkjfTXxCnPquFOdQb\nvj4l+UiB1ClMqyivLOqycqgm0mEv0sFVwunWdtasVPt8+3j1fvcXkWppoLRdq/ed\n";
    // salt=92104F0CDF88AEF5
    // key=0D0ACA1BAD75C312AF35539D2346EAE14A9D04A540DF60F2
    // iv =B990BA00BF3360D8
    private static String aes_192_cbc_string = "U2FsdGVkX1+G4oqSnvZzAvWbf+PVFxT9W+fr5B2x15gOyDid3O7Pl0KeJVeRkdNt\n6L89iZfyOFmv4fvAe4GXkf4va7naQAHmyD7SgUTt+hxnTkL2TEdrAMLj+OPuYi38\n";

    // salt=86E28A929EF67302
    // key=225B51B76BE7B81BE6315A7EA0CEA15CE66DC5345951C18D
    // iv =61EBF519574DDD88B69804CEDB4BE299

    @BeforeClass
    public static void initializeTestSuite() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void gen_key() throws Exception {
        log.debug(null);
        log.info("gen_key");
        final int keySize = 128;
        final String cipher = "AES";
        final Key sessionKey = CryptoKey.genKey(keySize, cipher);
        log.debug(sessionKey.toString());
        log.debug(Utils.encodeHexString_apache(sessionKey.getEncoded()));
        log.debug(sessionKey.getAlgorithm());
        log.debug(sessionKey.getFormat());

        Assert.assertTrue(true);
    }

    @Test
    public void encrypt_string() throws Exception {
        log.debug(null);
        log.info("encrypt_string");

        final int keySize = 192;
        final String cipher = "AES";
        final Key sessionKey = CryptoKey.genKey(keySize, cipher);
        final Crypto crypto = new Crypto(sessionKey);

        final SealedObject so = crypto.encrypt(clearString);
        log.debug(so.toString());

        final Object o = crypto.decrypt(so);
        log.debug(o.toString());

        Assert.assertEquals(clearString, o.toString());
    }

    @Test
    public void encrypt_file() throws Exception {
        log.debug(null);
        log.info("encrypt_file");

        final int keySize = 192;
        final String cipher = "AES";
        final Key sessionKey = CryptoKey.genKey(keySize, cipher);
        final Crypto crypto = new Crypto(sessionKey);

        writeClearFile();

        FileInputStream in = new FileInputStream(clearFile);
        FileOutputStream out = new FileOutputStream(cipherFile);
        crypto.encrypt(in, out);

        in = new FileInputStream(cipherFile);
        out = new FileOutputStream(clearFile);
        crypto.decrypt(in, out);

        final char[] cbuf = readFileChar(clearFile);
        log.debug(String.valueOf(cbuf));

        Assert.assertEquals(clearString, String.valueOf(cbuf));
    }

    @Test
    public void encrypt_pbe_salt() throws Exception {
        log.debug(null);
        log.info("encrypt_pbe_salt");

        final int keySize = 192;
        final String cipher = "PBEWITHSHA1AND192BITAES-CBC-BC";
        final String password = "caramels";
        SecureRandom.getInstance("NativePRNG").nextBytes(salt);
        final int iteration = 1;

        final SecretKey sessionKey = CryptoKey
            .genPBEKey(keySize, cipher, password, salt, iteration);
        final Crypto crypto = new Crypto(sessionKey);

        writeClearFile();
        FileInputStream in = new FileInputStream(clearFile);
        FileOutputStream out = new FileOutputStream(cipherFile);
        crypto.encrypt(in, out);

        if (base64) {
            final File tmpFile = new File("/tmp/encrypt_pbe_salt_tmp");
            Utils.encodeBase64Chunked(cipherFile, tmpFile);

            final char[] cbuf = readFileChar(tmpFile);
            log.debug(String.valueOf(cbuf));
        }

        in = new FileInputStream(cipherFile);
        out = new FileOutputStream(clearFile);
        crypto.decrypt(in, out);

        final char[] cbuf = readFileChar(clearFile);
        log.debug(String.valueOf(cbuf));

        Assert.assertEquals(clearString, String.valueOf(cbuf));
    }

    @Test
    public void decrypt_pbe_salt() throws Exception {
        log.debug(null);
        log.info("decrypt_pbe_salt");

        final int keySize = 192;
        final String cipher = "PBEWITHSHA1AND192BITAES-CBC-BC";
        final String password = "caramels";
        final int iteration = 1;

        final SecretKey sessionKey = CryptoKey
            .genPBEKey(keySize, cipher, password, salt, iteration);
        final Crypto crypto = new Crypto(sessionKey);

        final FileInputStream in = new FileInputStream(cipherFile);
        final FileOutputStream out = new FileOutputStream(clearFile);
        crypto.decrypt(in, out);

        final char[] cbuf = readFileChar(clearFile);
        log.debug(String.valueOf(cbuf));

        Assert.assertEquals(clearString, String.valueOf(cbuf));
    }

    @Test
    public void encrypt_openssl_des3() throws Exception {
        log.debug(null);
        log.info("encrypt_openssl_des3");

        cipherFile = new File("/tmp/encrypt_openssl_des3");
        writeClearFile();

        final FileInputStream in = new FileInputStream(clearFile);
        final FileOutputStream out = new FileOutputStream(cipherFile);

        Crypto.encrypt_openssl_des3(in, out, "caramels");

        final File tmpFile = new File("/tmp/encrypt_openssl_des3_tmp");
        Utils.encodeBase64Chunked(cipherFile, tmpFile);

        Assert.assertEquals(des_ede3_cbc_string.length(), tmpFile.length());
    }

    @Test
    public void decrypt_openssl_des3() throws Exception {
        log.debug(null);
        log.info("decrypt_openssl_des3");

        cipherFile = new File("/tmp/decrypt_openssl_des3");
        writeDES3File();

        final File tmpFile = new File("/tmp/decrypt_openssl_des3_tmp");
        Utils.decodeBase64(cipherFile, tmpFile);
        cipherFile = tmpFile;

        final FileInputStream in = new FileInputStream(tmpFile);
        final FileOutputStream out = new FileOutputStream(clearFile);

        Crypto.decrypt_openssl_des3(in, out, "caramels");

        Assert.assertEquals(clearString.length(), clearFile.length());

        final char[] cbuf = readFileChar(clearFile);
        Assert.assertEquals(clearString, String.valueOf(cbuf));
    }

    @Test
    public void encrypt_openssl_aes192() throws Exception {
        log.debug(null);
        log.info("encrypt_openssl_aes192");

        cipherFile = new File("/tmp/encrypt_openssl_aes192");
        writeClearFile();

        final FileInputStream in = new FileInputStream(clearFile);
        final FileOutputStream out = new FileOutputStream(cipherFile);

        Crypto.encrypt_openssl_aes192(in, out, "caramels");

        final File tmpFile = new File("/tmp/encrypt_openssl_aes192_tmp");
        Utils.encodeBase64Chunked(cipherFile, tmpFile);

        Assert.assertEquals(aes_192_cbc_string.length(), tmpFile.length());
    }

    @Test
    public void decrypt_openssl_aes192() throws Exception {
        log.debug(null);
        log.info("decrypt_openssl_aes192");

        cipherFile = new File("/tmp/decrypt_openssl_aes192");
        writeAES192File();

        final File tmpFile = new File("/tmp/decrypt_openssl_aes192_tmp");
        Utils.decodeBase64(cipherFile, tmpFile);

        final FileInputStream in = new FileInputStream(tmpFile);
        final FileOutputStream out = new FileOutputStream(clearFile);

        Crypto.decrypt_openssl_aes192(in, out, "caramels");

        Assert.assertEquals(clearString.length(), clearFile.length());

        final char[] cbuf = readFileChar(clearFile);
        Assert.assertEquals(clearString, String.valueOf(cbuf));
    }

    // ///////

    static void writeClearFile() throws Exception {
        final FileWriter fw = new FileWriter(clearFile);
        fw.write(clearString);
        fw.close();
    }

    static void writeAES192File() throws Exception {
        final FileWriter fw = new FileWriter(cipherFile);
        fw.write(aes_192_cbc_string);
        fw.close();
    }

    static void writeDES3File() throws Exception {
        final FileWriter fw = new FileWriter(cipherFile);
        fw.write(des_ede3_cbc_string);
        fw.close();
    }

    static char[] readFileChar(final File f) throws Exception {
        final FileReader fr = new FileReader(f);
        final char[] cbuf = new char[(int) f.length()];
        fr.read(cbuf);
        fr.close();
        return cbuf;
    }

    static byte[] readFileByte(final File f) throws Exception {
        final FileInputStream in = new FileInputStream(f);
        final byte[] bbuf = new byte[(int) f.length()];
        in.read(bbuf);
        in.close();
        return bbuf;
    }

}
