package demo.bc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

public final class CertTools {

    private static final Log log = LogFactory.getLog(CertTools.class);

    private CertTools() {}

    public static boolean isSelfSigned(final X509Certificate cert) {
        final boolean isSelfSigned = getSubject(cert).equals(getIssuer(cert));
        if (log.isDebugEnabled()) {
            log.debug("isSelfSigned: " + isSelfSigned);
        }
        return isSelfSigned;
    }

    public static X500Principal getSubject(final X509Certificate cert) {
        final X500Principal subject = cert.getSubjectX500Principal();
        if (log.isDebugEnabled()) {
            log.debug("getSubject: " + subject.toString());
        }
        return subject;
    }

    public static X500Principal getIssuer(final X509Certificate cert) {
        final X500Principal issuer = cert.getIssuerX500Principal();
        if (log.isDebugEnabled()) {
            log.debug("getIssuer: " + issuer.toString());
        }
        return issuer;
    }

    public static String getCommonName(final X500Principal principal) {
        final X509Name name = new X509Name(principal.getName());

        final String cn = getPartFromX509Name(name, X509Name.CN);
        if (log.isDebugEnabled()) {
            log.debug("getCommonName: " + cn);
        }
        return cn;
    }

    public static String getEmail(final X500Principal principal) {
        final X509Name name = new X509Name(principal.getName());

        String email = getPartFromX509Name(name, X509Name.EmailAddress);
        if (email != null) {
            if (log.isDebugEnabled()) {
                log.debug("email: " + email);
            }
            try {
                if (email.charAt(0) == '#') {
                    email = Utils.decodeHex(email.substring(5));
                }
            } catch (final Exception e) {}
        }
        if (log.isDebugEnabled()) {
            log.debug("getEmail: " + email);
        }
        return email;
    }

    public static String getPartFromX509Name(final X509Name name, final DERObjectIdentifier oid) {
        if (name == null) return null;
        int ix;
        if ((ix = name.getOIDs().indexOf(oid)) != -1) {
            final Object val = name.getValues().get(ix);
            if (val != null) return val.toString();
        }
        return null;
    }

    public static void writePEMObject(final Object obj, final File f) throws Exception {
        final PEMWriter pem = new PEMWriter(new FileWriter(f));
        pem.writeObject(obj);
        pem.flush();
        pem.close();
    }

    public static void writePEMObject(final Object obj, final File f, final String passwd)
        throws Exception {
        final PEMWriter pem = new PEMWriter(new FileWriter(f));
        pem.writeObject(obj, "AES-192-CBC", passwd.toCharArray(), new SecureRandom());
        pem.flush();
        pem.close();
    }

    public static Object readPEMObject(final File f) throws Exception {
        final PEMReader pem = new PEMReader(new FileReader(f));
        final Object o = pem.readObject();
        pem.close();
        return o;
    }

    public static Object readPEMObject(final File f, final String passwd) throws Exception {
        final PasswordFinder passwordFinder = new PasswordFinder() {
            public char[] getPassword() {
                return passwd.toCharArray();
            }
        };
        final PEMReader pem = new PEMReader(new FileReader(f), passwordFinder);
        final Object o = pem.readObject();
        pem.close();
        return o;
    }

    public static Certificate[] getCertificateChainP12(final File p12file, final char[] passwd,
        final String alias) throws Exception {
        final KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");
        keystore.load(new FileInputStream(p12file), passwd);
        return keystore.getCertificateChain(alias);
    }

    public static Key getKeyP12(final File p12file, final char[] passwd, final String alias,
        final char[] keypasswd) throws Exception {
        final KeyStore keystore = KeyStore.getInstance("PKCS12", "BC");
        keystore.load(new FileInputStream(p12file), passwd);
        return keystore.getKey(alias, keypasswd);
    }

    public static Certificate[] getCertificateChainJKS(final File jksfile, final char[] passwd,
        final String alias) throws Exception {
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(jksfile), passwd);
        return keystore.getCertificateChain(alias);
    }

    public static Key getKeyJKS(final File jksfile, final char[] passwd, final String alias,
        final char[] keypasswd) throws Exception {
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(jksfile), passwd);
        return keystore.getKey(alias, keypasswd);
    }

}
