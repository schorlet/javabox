package demo.bc;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignedObject;

public final class CertSign {

    private CertSign() {}

    public static byte[] createSignature(final PrivateKey key, final byte[] buffer)
        throws Exception {
        final Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(key);
        sig.update(buffer, 0, buffer.length);
        return sig.sign();
    }

    public static boolean verifySignature(final PublicKey key, final byte[] buffer,
        final byte[] signature) throws Exception {
        // Signature sig = Signature.getInstance(key.getAlgorithm());
        final Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(key);
        sig.update(buffer, 0, buffer.length);
        return sig.verify(signature);
    }

    public static byte[] createDigest(final File file) throws Exception {
        final FileInputStream fis = new FileInputStream(file);
        final MessageDigest sha1 = MessageDigest.getInstance("SHA1");

        final byte[] buffer = new byte[4];
        int i = 0;
        while ((i = fis.read(buffer)) >= 0) {
            sha1.update(buffer, 0, i);
        }
        fis.close();

        return sha1.digest();
    }

    public static SignedObject createSignature(final PrivateKey key, final Serializable object)
        throws Exception {
        final Signature signature = Signature.getInstance(key.getAlgorithm());
        return new SignedObject(object, key, signature);
    }

    public static boolean verifySignature(final PublicKey key, final SignedObject signedObject)
        throws Exception {
        final Signature signature = Signature.getInstance(key.getAlgorithm());
        return signedObject.verify(key, signature);
    }
}
