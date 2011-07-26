package demo.bc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public final class CertKey {
    private static final Log log = LogFactory.getLog(CertKey.class);
    private static final String algorithm = "RSA";

    private CertKey() {}

    public static KeyPair generateKeyPair(final int keySize) throws Exception {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(keySize);
        if (log.isDebugEnabled()) {
            log.debug(keyPairGenerator.toString());
            log.debug("Algorithm = " + keyPairGenerator.getAlgorithm());
            log.debug("Provider = " + keyPairGenerator.getProvider());
        }
        return keyPairGenerator.generateKeyPair();
    }

    public static KeyPair readKeyPair(final File privateKey) throws Exception {
        final Object o = CertTools.readPEMObject(privateKey);
        final KeyPair keyPair = (KeyPair) o;
        if (log.isDebugEnabled()) {
            final PrivateKey key = keyPair.getPrivate();
            log.debug(key.toString());
            final PublicKey pubKey = keyPair.getPublic();
            log.debug(pubKey.toString());
        }
        return keyPair;
    }

    public static KeyPair readKeyPair(final File privateKey, final String passwd) throws Exception {
        final Object o = CertTools.readPEMObject(privateKey, passwd);
        final KeyPair keyPair = (KeyPair) o;
        if (log.isDebugEnabled()) {
            final PrivateKey key = keyPair.getPrivate();
            log.debug(key.toString());
            final PublicKey pubKey = keyPair.getPublic();
            log.debug(pubKey.toString());
        }
        return keyPair;
    }

    public static PublicKey readPublicKey(final File publicKey) throws Exception {
        final Object o = CertTools.readPEMObject(publicKey);
        final PublicKey pubKey = (PublicKey) o;
        if (log.isDebugEnabled()) {
            log.debug(pubKey.toString());
        }
        return pubKey;
    }

    public static void writeKeyPair(final KeyPair pair, final File privateKeyFile,
        final File publicKeyFile) throws Exception {
        CertTools.writePEMObject(pair.getPrivate(), privateKeyFile);
        CertTools.writePEMObject(pair.getPublic(), publicKeyFile);
    }

    public static void writeKeyPair(final KeyPair pair, final File privateKeyFile,
        final File publicKeyFile, final String passwd) throws Exception {
        CertTools.writePEMObject(pair.getPrivate(), privateKeyFile, passwd);
        CertTools.writePEMObject(pair.getPublic(), publicKeyFile);
    }

    // public KeyPair readKeyPair(File privateKeyFile,
    // File publicKeyFile) throws Exception {
    // KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
    // byte[] priKeyBytes = CertTools.readKeyPair(privateKeyFile);
    // PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(priKeyBytes);
    // PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);
    // byte[] pubKeyBytes = CertTools.readPublicKey(publicKeyFile);
    // X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
    // PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
    // return new KeyPair(pubKey, priKey);
    // }

    public static SubjectKeyIdentifier createSubjectKeyId(final PublicKey pubKey) throws Exception {
        final ByteArrayInputStream bais = new ByteArrayInputStream(pubKey.getEncoded());
        final SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
            (ASN1Sequence) new ASN1InputStream(bais).readObject());
        return new SubjectKeyIdentifier(info);
    }

    public static AuthorityKeyIdentifier createAuthorityKeyId(final PublicKey pubKey)
        throws Exception {
        final ByteArrayInputStream bais = new ByteArrayInputStream(pubKey.getEncoded());
        final SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(
            (ASN1Sequence) new ASN1InputStream(bais).readObject());
        return new AuthorityKeyIdentifier(info);
    }

}
