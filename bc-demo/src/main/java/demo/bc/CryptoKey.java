package demo.bc;

import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public final class CryptoKey {
    private static final Log log = LogFactory.getLog(CryptoKey.class);

    private CryptoKey() {}

    public static Key genKey(final int keysize, final String cipher) throws Exception {
        final KeyGenerator generator = KeyGenerator.getInstance(cipher);
        generator.init(keysize);
        final Key encryptionKey = generator.generateKey();

        log.debug("encryptionKey.Encoded="
            + Utils.encodeHexString_apache(encryptionKey.getEncoded()));
        log.debug("encryptionKey.length=" + encryptionKey.getEncoded().length);
        log.debug("encryptionKey.Algorithm=" + encryptionKey.getAlgorithm());
        return encryptionKey;
    }

    public static SecretKey genPBEKey(final int keysize, final String cipher,
        final String password, final byte[] salt, final int iteration) throws Exception {
        final PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iteration, keysize);
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(cipher);
        final SecretKey secretKey = keyFactory.generateSecret(keySpec);

        log.debug("keyFactory=" + keyFactory);
        log.debug("secretKey=" + secretKey);
        log.debug("salt=" + Utils.encodeHexString_apache(salt));
        log.debug("salt.length=" + salt.length);
        log.debug("secretKey.Encoded=" + Utils.encodeHexString_apache(secretKey.getEncoded()));
        log.debug("secretKey.length=" + secretKey.getEncoded().length);
        log.debug("secretKey.Algorithm=" + secretKey.getAlgorithm());
        return secretKey;
    }

    public static ParametersWithIV genDES3OpenSSLKey(final String password, final byte[] salt) {
        final int keylen = 192;
        final int ivlen = 64;
        final int iterationCount = 1;
        return genOpenSSLKey(password, salt, keylen, ivlen, iterationCount);
    }

    public static ParametersWithIV genAES192OpenSSLKey(final String password, final byte[] salt) {
        final int keylen = 192;
        final int ivlen = 128;
        final int iterationCount = 1;
        return genOpenSSLKey(password, salt, keylen, ivlen, iterationCount);
    }

    public static ParametersWithIV genOpenSSLKey(final String password, final byte[] salt,
        final int keylen, final int ivlen, final int iterationCount) {
        // creating generator for PBE derived keys and ivs as used by openssl
        final PBEParametersGenerator generator = new OpenSSLPBEParametersGenerator();

        // intialse the PBE generator with password, salt and iteration count
        generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(password.toCharArray()), salt,
            iterationCount);

        // Generate a key with initialisation vector parameter derived from the
        // password, salt and iteration count
        final ParametersWithIV parametersWithIV = (ParametersWithIV) generator
            .generateDerivedParameters(keylen, ivlen);

        final KeyParameter keyParam = (KeyParameter) parametersWithIV.getParameters();
        log.debug("salt: " + Utils.encodeHexString_apache(salt));
        log.debug("key : " + Utils.encodeHexString_apache(keyParam.getKey()));
        log.debug("iv  : " + Utils.encodeHexString_apache(parametersWithIV.getIV()));

        return parametersWithIV;
    }

}
