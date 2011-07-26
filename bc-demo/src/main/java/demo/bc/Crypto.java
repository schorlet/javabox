package demo.bc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SealedObject;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public final class Crypto {
    private static final Log log = LogFactory.getLog(Crypto.class);

    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;

    public Crypto(final Key sessionKey) throws Exception {
        encryptCipher = Cipher.getInstance(sessionKey.getAlgorithm());
        encryptCipher.init(Cipher.ENCRYPT_MODE, sessionKey);

        decryptCipher = Cipher.getInstance(sessionKey.getAlgorithm());
        decryptCipher.init(Cipher.DECRYPT_MODE, sessionKey);

        log.debug("Cipher=" + encryptCipher.toString());
        log.debug("Cipher.Algorithm=" + encryptCipher.getAlgorithm());
        log.debug("Cipher.IV=" + Utils.encodeHexString_apache(encryptCipher.getIV()));
        log.debug("Cipher.BlockSize=" + encryptCipher.getBlockSize());
        log.debug("Cipher.Parameters=" + encryptCipher.getParameters());
    }

    public Crypto(final Key sessionKey, final AlgorithmParameterSpec param) throws Exception {
        encryptCipher = Cipher.getInstance(sessionKey.getAlgorithm());
        encryptCipher.init(Cipher.ENCRYPT_MODE, sessionKey, param);

        decryptCipher = Cipher.getInstance(sessionKey.getAlgorithm());
        decryptCipher.init(Cipher.DECRYPT_MODE, sessionKey, param);

        log.debug("Cipher=" + encryptCipher);
        log.debug("Cipher.Algorithm=" + encryptCipher.getAlgorithm());
        log.debug("Cipher.IV=" + Utils.encodeHexString_apache(encryptCipher.getIV()));
        log.debug("Cipher.BlockSize=" + encryptCipher.getBlockSize());
        log.debug("Cipher.Parameters=" + encryptCipher.getParameters());
    }

    public SealedObject encrypt(final Serializable object) throws Exception {
        return new SealedObject(object, encryptCipher);
    }

    public Object decrypt(final SealedObject object) throws Exception {
        return object.getObject(decryptCipher);
    }

    public void encrypt(final InputStream in, OutputStream out) throws Exception {
        final byte[] buf = new byte[1024];
        out = new CipherOutputStream(out, encryptCipher);
        int numRead = 0;
        while ((numRead = in.read(buf)) != -1) {
            out.write(buf, 0, numRead);
        }
        out.flush();
        out.close();
    }

    public void decrypt(InputStream in, final OutputStream out) throws Exception {
        final byte[] buf = new byte[1024];
        in = new CipherInputStream(in, decryptCipher);
        int numRead = 0;
        while ((numRead = in.read(buf)) != -1) {
            out.write(buf, 0, numRead);
        }
        out.flush();
        out.close();
    }

    public static void encrypt_openssl_des3(final InputStream in, OutputStream out,
        final String password) throws Exception {
        final byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);

        final ParametersWithIV paramWithIv = CryptoKey.genDES3OpenSSLKey(password, salt);
        final IvParameterSpec iv = new IvParameterSpec(paramWithIv.getIV());
        final byte[] key = ((KeyParameter) paramWithIv.getParameters()).getKey();
        final SecretKeySpec keySpec = new SecretKeySpec(key, "DESede");

        final Cipher bbc = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        bbc.init(Cipher.ENCRYPT_MODE, keySpec, iv);

        out.write("Salted__".getBytes());
        out.write(salt);

        final byte[] buf = new byte[1024];
        out = new CipherOutputStream(out, bbc);
        int numRead = 0;
        while ((numRead = in.read(buf)) != -1) {
            out.write(buf, 0, numRead);
        }
        out.flush();
        out.close();
    }

    public static void decrypt_openssl_des3(InputStream in, final OutputStream out,
        final String password) throws Exception {
        final byte[] salt = new byte[8];
        in.read(salt);
        in.read(salt);

        final ParametersWithIV paramWithIv = CryptoKey.genDES3OpenSSLKey(password, salt);
        final IvParameterSpec iv = new IvParameterSpec(paramWithIv.getIV());
        final byte[] key = ((KeyParameter) paramWithIv.getParameters()).getKey();
        final SecretKeySpec keySpec = new SecretKeySpec(key, "DESede");

        final Cipher bbc = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        bbc.init(Cipher.DECRYPT_MODE, keySpec, iv);

        final byte[] buf = new byte[1024];
        in = new CipherInputStream(in, bbc);
        int numRead = 0;
        while ((numRead = in.read(buf)) != -1) {
            out.write(buf, 0, numRead);
        }
        out.flush();
        out.close();
    }

    public static void encrypt_openssl_aes192(final InputStream in, OutputStream out,
        final String password) throws Exception {
        final byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);

        final ParametersWithIV paramWithIv = CryptoKey.genAES192OpenSSLKey(password, salt);
        final IvParameterSpec iv = new IvParameterSpec(paramWithIv.getIV());
        final byte[] key = ((KeyParameter) paramWithIv.getParameters()).getKey();
        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        final Cipher bbc = Cipher.getInstance("AES/CBC/PKCS5Padding");
        bbc.init(Cipher.ENCRYPT_MODE, keySpec, iv);

        out.write("Salted__".getBytes());
        out.write(salt);

        final byte[] buf = new byte[1024];
        out = new CipherOutputStream(out, bbc);
        int numRead = 0;
        while ((numRead = in.read(buf)) != -1) {
            out.write(buf, 0, numRead);
        }
        out.flush();
        out.close();
    }

    public static void decrypt_openssl_aes192(InputStream in, final OutputStream out,
        final String password) throws Exception {
        final byte[] salt = new byte[8];
        in.read(salt);
        in.read(salt);

        final ParametersWithIV paramWithIv = CryptoKey.genAES192OpenSSLKey(password, salt);
        final IvParameterSpec iv = new IvParameterSpec(paramWithIv.getIV());
        final byte[] key = ((KeyParameter) paramWithIv.getParameters()).getKey();
        final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        final Cipher bbc = Cipher.getInstance("AES/CBC/PKCS5Padding");
        bbc.init(Cipher.DECRYPT_MODE, keySpec, iv);

        final byte[] buf = new byte[1024];
        in = new CipherInputStream(in, bbc);
        int numRead = 0;
        while ((numRead = in.read(buf)) != -1) {
            out.write(buf, 0, numRead);
        }
        out.flush();
        out.close();
    }

    public static void encrypt_openssl_des3_byte(final InputStream in, final OutputStream out,
        final String password) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        int i = 0;
        while ((i = in.read(buf)) != -1) {
            baos.write(buf, 0, i);
        }
        final byte[] data = baos.toByteArray();
        baos.close();

        final DESedeEngine blockCipher = new DESedeEngine();
        blockCipher.reset();
        final CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
        final BlockCipherPadding blockcipherpadding = new PKCS7Padding();
        final BufferedBlockCipher bbc = new PaddedBufferedBlockCipher(cbcCipher, blockcipherpadding);

        final byte[] salt = new byte[8];
        new SecureRandom().nextBytes(salt);

        // intialising in the encryption mode with Key and IV
        bbc.init(true, CryptoKey.genDES3OpenSSLKey(password, salt));
        final byte[] encryptedData = new byte[bbc.getOutputSize(data.length)];
        log.debug("data.length=" + data.length);
        log.debug("encryptedData.length=" + encryptedData.length);

        // process array of bytes
        int noOfBytes = bbc.processBytes(data, 0, data.length, encryptedData, 0);
        log.debug("noOfBytes.length=" + noOfBytes);
        // process the last block in the buffer
        noOfBytes += bbc.doFinal(encryptedData, noOfBytes);
        log.debug("noOfBytes.length=" + noOfBytes);

        // writing encrypted data along with the salt in the format readable by
        // open ssl api
        out.write("Salted__".getBytes());
        out.write(salt);
        out.write(encryptedData, 0, noOfBytes);
        out.flush();
        out.close();
    }

    public static void decrypt_openssl_des3_byte(final InputStream in, final OutputStream out,
        final String password) throws Exception {
        final DESedeEngine blockCipher = new DESedeEngine();
        blockCipher.reset();
        final CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
        final BlockCipherPadding blockcipherpadding = new PKCS7Padding();
        final BufferedBlockCipher bbc = new PaddedBufferedBlockCipher(cbcCipher, blockcipherpadding);

        final byte[] salt = new byte[8];
        in.read(salt);
        in.read(salt);
        bbc.init(false, CryptoKey.genDES3OpenSSLKey(password, salt));

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        int i = 0;
        while ((i = in.read(buf)) != -1) {
            baos.write(buf, 0, i);
        }
        final byte[] data = baos.toByteArray();
        baos.close();

        // intialising in the decryption mode with Key and IV
        final byte[] decryptedData = new byte[bbc.getOutputSize(data.length)];

        log.debug("data.length=" + data.length);
        log.debug("decryptedData.length=" + decryptedData.length);

        // process array of bytes
        int noOfBytes = bbc.processBytes(data, 0, data.length, decryptedData, 0);
        log.debug("noOfBytes.length=" + noOfBytes);
        // process the last block in the buffer
        noOfBytes += bbc.doFinal(decryptedData, noOfBytes);
        log.debug("noOfBytes.length=" + noOfBytes);

        // writing decrypted data
        out.write(decryptedData, 0, noOfBytes);
        out.flush();
        out.close();
    }

}
