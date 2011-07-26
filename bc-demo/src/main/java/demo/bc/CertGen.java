package demo.bc;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

public final class CertGen {
    private static final Log log = LogFactory.getLog(CertGen.class);

    static X509V1CertificateGenerator v1CertGen = new X509V1CertificateGenerator();
    static X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();

    private CertGen() {}

    public static X509Certificate genV1Cert(final KeyPair caKeyPair, final X509Principal subjectDN)
        throws Exception {
        v1CertGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        v1CertGen.setIssuerDN(subjectDN);
        v1CertGen.setNotBefore(new Date(System.currentTimeMillis()));
        v1CertGen.setNotAfter(new Date(System.currentTimeMillis() + 31536000000L));
        v1CertGen.setSubjectDN(subjectDN);
        v1CertGen.setPublicKey(caKeyPair.getPublic());
        v1CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");
        final X509Certificate x509certificate = v1CertGen.generate(caKeyPair.getPrivate());
        x509certificate.checkValidity(new Date());
        x509certificate.verify(caKeyPair.getPublic());
        if (log.isDebugEnabled()) {
            log.debug(x509certificate.toString());
        }
        return x509certificate;
    }

    public static X509Certificate genV3SslCACert(final KeyPair keyPair,
        final X509Principal subjectDN) throws Exception {
        final Hashtable<DERObjectIdentifier, DEREncodable> criticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();
        final Hashtable<DERObjectIdentifier, DEREncodable> nonCriticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();

        final DEREncodable basicConstraints = new BasicConstraints(true);
        final DEREncodable authorityKeyIdentifier = new AuthorityKeyIdentifierStructure(
            keyPair.getPublic());
        final DEREncodable subjectKeyIdentifier = new SubjectKeyIdentifierStructure(
            keyPair.getPublic());
        final DEREncodable keyUsage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign);
        final DEREncodable netscapeCertType = new NetscapeCertType(NetscapeCertType.sslCA);
        final DEREncodable netscapeCertComment = new DERIA5String("OpenSSL CA Certificate");

        criticalExtensions.put(X509Extensions.BasicConstraints, basicConstraints);
        criticalExtensions.put(MiscObjectIdentifiers.netscapeCertType, netscapeCertType);
        criticalExtensions.put(X509Extensions.KeyUsage, keyUsage);
        nonCriticalExtensions.put(X509Extensions.AuthorityKeyIdentifier, authorityKeyIdentifier);
        nonCriticalExtensions.put(X509Extensions.SubjectKeyIdentifier, subjectKeyIdentifier);
        nonCriticalExtensions.put(MiscObjectIdentifiers.netscapeCertComment, netscapeCertComment);

        return genCert(keyPair, subjectDN, keyPair, subjectDN, criticalExtensions,
            nonCriticalExtensions);
    }

    public static X509Certificate genV3SslServerCert(final KeyPair subjectKeyPair,
        final X509Principal subjectDN, final KeyPair parentKeyPair, final X509Principal parentDN)
        throws Exception {
        final Hashtable<DERObjectIdentifier, DEREncodable> criticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();
        final Hashtable<DERObjectIdentifier, DEREncodable> nonCriticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();

        final DEREncodable basicConstraints = new BasicConstraints(false);
        final DEREncodable authorityKeyIdentifier = new AuthorityKeyIdentifierStructure(
            parentKeyPair.getPublic());
        final DEREncodable subjectKeyIdentifier = new SubjectKeyIdentifierStructure(
            subjectKeyPair.getPublic());
        final DEREncodable keyUsage = new KeyUsage(KeyUsage.keyAgreement | KeyUsage.keyEncipherment);
        final DEREncodable extendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth);
        final DEREncodable netscapeCertType = new NetscapeCertType(NetscapeCertType.sslServer);
        final DEREncodable netscapeCertComment = new DERIA5String(
            "OpenSSL Certificate for SSL Web Server");

        criticalExtensions.put(X509Extensions.BasicConstraints, basicConstraints);
        criticalExtensions.put(MiscObjectIdentifiers.netscapeCertType, netscapeCertType);
        criticalExtensions.put(X509Extensions.KeyUsage, keyUsage);
        nonCriticalExtensions.put(X509Extensions.AuthorityKeyIdentifier, authorityKeyIdentifier);
        nonCriticalExtensions.put(X509Extensions.SubjectKeyIdentifier, subjectKeyIdentifier);
        nonCriticalExtensions.put(MiscObjectIdentifiers.netscapeCertComment, netscapeCertComment);
        criticalExtensions.put(X509Extensions.ExtendedKeyUsage, extendedKeyUsage);

        return genCert(subjectKeyPair, subjectDN, parentKeyPair, parentDN, criticalExtensions,
            nonCriticalExtensions);
    }

    public static X509Certificate genV3SslClientCert(final KeyPair subjectKeyPair,
        final X509Principal subjectDN, final KeyPair parentKeyPair, final X509Principal parentDN)
        throws Exception {
        final Hashtable<DERObjectIdentifier, DEREncodable> criticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();
        final Hashtable<DERObjectIdentifier, DEREncodable> nonCriticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();

        final DEREncodable basicConstraints = new BasicConstraints(false);
        final DEREncodable authorityKeyIdentifier = new AuthorityKeyIdentifierStructure(
            parentKeyPair.getPublic());
        final DEREncodable subjectKeyIdentifier = new SubjectKeyIdentifierStructure(
            subjectKeyPair.getPublic());
        final DEREncodable keyUsage = new KeyUsage(KeyUsage.digitalSignature);
        final DEREncodable extendedKeyUsage = new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth);
        final DEREncodable netscapeCertType = new NetscapeCertType(NetscapeCertType.sslClient);
        final DEREncodable netscapeCertComment = new DERIA5String(
            "OpenSSL Certificate for SSL Client");

        criticalExtensions.put(X509Extensions.BasicConstraints, basicConstraints);
        criticalExtensions.put(MiscObjectIdentifiers.netscapeCertType, netscapeCertType);
        criticalExtensions.put(X509Extensions.KeyUsage, keyUsage);
        nonCriticalExtensions.put(X509Extensions.AuthorityKeyIdentifier, authorityKeyIdentifier);
        nonCriticalExtensions.put(X509Extensions.SubjectKeyIdentifier, subjectKeyIdentifier);
        nonCriticalExtensions.put(MiscObjectIdentifiers.netscapeCertComment, netscapeCertComment);
        criticalExtensions.put(X509Extensions.ExtendedKeyUsage, extendedKeyUsage);

        return genCert(subjectKeyPair, subjectDN, parentKeyPair, parentDN, criticalExtensions,
            nonCriticalExtensions);
    }

    public static X509Certificate genV3SigningCert(final KeyPair subjectKeyPair,
        final X509Principal subjectDN, final KeyPair parentKeyPair, final X509Principal parentDN)
        throws Exception {
        final Hashtable<DERObjectIdentifier, DEREncodable> criticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();
        final Hashtable<DERObjectIdentifier, DEREncodable> nonCriticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();

        final DEREncodable basicConstraints = new BasicConstraints(false);
        final DEREncodable authorityKeyIdentifier = new AuthorityKeyIdentifierStructure(
            parentKeyPair.getPublic());
        final DEREncodable subjectKeyIdentifier = new SubjectKeyIdentifierStructure(
            subjectKeyPair.getPublic());
        final DEREncodable keyUsage = new KeyUsage(KeyUsage.nonRepudiation
            | KeyUsage.digitalSignature);
        final DEREncodable extendedKeyUsage = new ExtendedKeyUsage(
            KeyPurposeId.id_kp_emailProtection);
        final DEREncodable netscapeCertType = new NetscapeCertType(NetscapeCertType.smime);
        final DEREncodable netscapeCertComment = new DERIA5String(
            "OpenSSL Certificate for Mail Protection");

        criticalExtensions.put(X509Extensions.BasicConstraints, basicConstraints);
        criticalExtensions.put(MiscObjectIdentifiers.netscapeCertType, netscapeCertType);
        criticalExtensions.put(X509Extensions.KeyUsage, keyUsage);
        nonCriticalExtensions.put(X509Extensions.AuthorityKeyIdentifier, authorityKeyIdentifier);
        nonCriticalExtensions.put(X509Extensions.SubjectKeyIdentifier, subjectKeyIdentifier);
        nonCriticalExtensions.put(MiscObjectIdentifiers.netscapeCertComment, netscapeCertComment);
        criticalExtensions.put(X509Extensions.ExtendedKeyUsage, extendedKeyUsage);

        return genCert(subjectKeyPair, subjectDN, parentKeyPair, parentDN, criticalExtensions,
            nonCriticalExtensions);
    }

    public static X509Certificate genV3EncipherCert(final KeyPair subjectKeyPair,
        final X509Principal subjectDN, final KeyPair parentKeyPair, final X509Principal parentDN)
        throws Exception {
        final Hashtable<DERObjectIdentifier, DEREncodable> criticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();
        final Hashtable<DERObjectIdentifier, DEREncodable> nonCriticalExtensions = new Hashtable<DERObjectIdentifier, DEREncodable>();

        final DEREncodable basicConstraints = new BasicConstraints(false);
        final DEREncodable authorityKeyIdentifier = new AuthorityKeyIdentifierStructure(
            parentKeyPair.getPublic());
        final DEREncodable subjectKeyIdentifier = new SubjectKeyIdentifierStructure(
            subjectKeyPair.getPublic());
        final DEREncodable keyUsage = new KeyUsage(KeyUsage.keyEncipherment);
        final DEREncodable extendedKeyUsage = new ExtendedKeyUsage(
            KeyPurposeId.id_kp_emailProtection);
        final DEREncodable netscapeCertType = new NetscapeCertType(NetscapeCertType.smime);
        final DEREncodable netscapeCertComment = new DERIA5String(
            "OpenSSL Certificate for Mail Protection");

        criticalExtensions.put(X509Extensions.BasicConstraints, basicConstraints);
        criticalExtensions.put(MiscObjectIdentifiers.netscapeCertType, netscapeCertType);
        criticalExtensions.put(X509Extensions.KeyUsage, keyUsage);
        nonCriticalExtensions.put(X509Extensions.AuthorityKeyIdentifier, authorityKeyIdentifier);
        nonCriticalExtensions.put(X509Extensions.SubjectKeyIdentifier, subjectKeyIdentifier);
        nonCriticalExtensions.put(MiscObjectIdentifiers.netscapeCertComment, netscapeCertComment);
        criticalExtensions.put(X509Extensions.ExtendedKeyUsage, extendedKeyUsage);

        return genCert(subjectKeyPair, subjectDN, parentKeyPair, parentDN, criticalExtensions,
            nonCriticalExtensions);
    }

    @SuppressWarnings("unchecked")
    private static X509Certificate genCert(final KeyPair subjectKeyPair,
        final X509Principal subjectDN, final KeyPair parentKeyPair, final X509Principal parentDN,
        final Hashtable<DERObjectIdentifier, DEREncodable> criticalExtensions,
        final Hashtable<DERObjectIdentifier, DEREncodable> nonCriticalExtensions) throws Exception {

        v3CertGen.reset();
        v3CertGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        v3CertGen.setIssuerDN(parentDN);
        v3CertGen.setNotBefore(new Date(System.currentTimeMillis()));
        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + 31536000000L));
        v3CertGen.setSubjectDN(subjectDN);
        v3CertGen.setPublicKey(subjectKeyPair.getPublic());
        v3CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

        if (criticalExtensions != null && criticalExtensions.size() > 0) {
            for (final Object element : criticalExtensions.entrySet()) {
                final Map.Entry<DERObjectIdentifier, DEREncodable> entry = (Map.Entry<DERObjectIdentifier, DEREncodable>) element;
                v3CertGen.addExtension(entry.getKey(), true, entry.getValue());
            }
        }
        if (nonCriticalExtensions != null && nonCriticalExtensions.size() > 0) {
            for (final Object element : nonCriticalExtensions.entrySet()) {
                final Map.Entry<DERObjectIdentifier, DEREncodable> entry = (Map.Entry<DERObjectIdentifier, DEREncodable>) element;
                v3CertGen.addExtension(entry.getKey(), false, entry.getValue());
            }
        }

        final X509Certificate x509certificate = v3CertGen.generate(parentKeyPair.getPrivate());
        x509certificate.checkValidity(new Date());
        x509certificate.verify(parentKeyPair.getPublic());
        if (log.isDebugEnabled()) {
            log.debug(x509certificate.toString());
        }
        return x509certificate;
    }

    public static void genP12(final String alias, final String password,
        final PrivateKey subjectKey, final X509Certificate subjectCertificate,
        final X509Certificate parentCertificate, final File out) throws Exception {
        final KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        store.load(null, null);

        if (!subjectCertificate.equals(parentCertificate)) {
            if (CertTools.isSelfSigned(parentCertificate)) {
                store.setCertificateEntry(
                    CertTools.getCommonName(parentCertificate.getSubjectX500Principal()),
                    parentCertificate);
            }
        }

        final X509Certificate[] chain = new X509Certificate[] { subjectCertificate,
            parentCertificate };
        store.setKeyEntry(alias, subjectKey, null, chain);

        final FileOutputStream fos = new FileOutputStream(out);
        store.store(fos, password.toCharArray());
        fos.close();
    }

    public static void genJKS(final String alias, final String password,
        final PrivateKey subjectKey, final X509Certificate subjectCertificate,
        final X509Certificate parentCertificate, final File out) throws Exception {
        final KeyStore store = KeyStore.getInstance("JKS");
        store.load(null, null);

        if (!subjectCertificate.equals(parentCertificate)) {
            if (CertTools.isSelfSigned(parentCertificate)) {
                store.setCertificateEntry(
                    CertTools.getCommonName(parentCertificate.getSubjectX500Principal()),
                    parentCertificate);
            }
        }

        final X509Certificate[] chain = new X509Certificate[] { subjectCertificate,
            parentCertificate };
        store.setKeyEntry(alias, subjectKey, password.toCharArray(), chain);

        final FileOutputStream fos = new FileOutputStream(out);
        store.store(fos, password.toCharArray());
        fos.close();
    }

}
