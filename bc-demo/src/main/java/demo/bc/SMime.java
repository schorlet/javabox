package demo.bc;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;

public final class SMime {
    private SMime() {}

    public static void createSignedMail(final File p12file, final char[] passwd,
        final String alias, final char[] keypasswd) throws Exception {
        final Certificate[] certchain = CertTools.getCertificateChainP12(p12file, passwd, alias);
        final PrivateKey privateKey = (PrivateKey) CertTools.getKeyP12(p12file, passwd, alias,
            keypasswd);

        final X500Principal principal = CertTools.getSubject((X509Certificate) certchain[0]);
        final String emailfrom = CertTools.getEmail(principal);
        if (emailfrom == null)
            throw new Exception(String.format("can't get email information from: %s",
                principal.toString()));

        final SMIMESignedGenerator generator = new SMIMESignedGenerator();
        generator.addSigner(privateKey, (X509Certificate) certchain[0],
            SMIMESignedGenerator.DIGEST_SHA1);

        final List<Certificate> arraylist = Arrays.asList(certchain);
        final CertStore certstore = CertStore.getInstance("Collection",
            new CollectionCertStoreParameters(arraylist));
        generator.addCertificatesAndCRLs(certstore);

        final MimeBodyPart mimebodypart = new MimeBodyPart();
        mimebodypart.setText("hello toto");
        final MimeMultipart mimemultipart = generator.generate(mimebodypart, "BC");

        final Properties properties = System.getProperties();
        final Session session = Session.getDefaultInstance(properties, null);
        final MimeMessage mimemessage = new MimeMessage(session);
        mimemessage.setFrom(new InternetAddress(emailfrom));
        mimemessage.setRecipient(Message.RecipientType.TO, new InternetAddress("to@www.net"));
        mimemessage.setSubject("example signed message");
        mimemessage.setContent(mimemultipart, mimemultipart.getContentType());
        mimemessage.saveChanges();
        mimemessage.writeTo(new FileOutputStream("/tmp/signed.eml"));
    }

    public static void createEncryptedMail(final X509Certificate cert) throws Exception {
        final X500Principal principal = CertTools.getSubject(cert);
        final String emailto = CertTools.getEmail(principal);
        if (emailto == null)
            throw new Exception(String.format("can't get email information from: %s",
                principal.toString()));

        final SMIMEEnvelopedGenerator generator = new SMIMEEnvelopedGenerator();
        generator.addKeyTransRecipient(cert);

        final MimeBodyPart mimebodypart = new MimeBodyPart();
        mimebodypart.setText("hello toto");
        final MimeBodyPart mimebodypart1 = generator.generate(mimebodypart,
            SMIMEEnvelopedGenerator.AES192_CBC, "BC");

        final Properties properties = System.getProperties();
        final Session session = Session.getDefaultInstance(properties, null);
        final MimeMessage mimemessage = new MimeMessage(session);
        mimemessage.setFrom(new InternetAddress("me@www.net"));
        mimemessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailto));
        mimemessage.setSubject("example encrypted message");
        mimemessage.setContent(mimebodypart1.getContent(), mimebodypart1.getContentType());
        mimemessage.saveChanges();
        mimemessage.writeTo(new FileOutputStream("/tmp/encrypted.eml"));
    }

    public static void createSignedAndEncryptedMail(final File p12file, final char[] passwd,
        final String alias, final char[] keypasswd, final X509Certificate cert) throws Exception {
        final Certificate[] certchain = CertTools.getCertificateChainP12(p12file, passwd, alias);
        final PrivateKey privateKey = (PrivateKey) CertTools.getKeyP12(p12file, passwd, alias,
            keypasswd);

        final X500Principal principal_from = CertTools.getSubject((X509Certificate) certchain[0]);
        final String emailfrom = CertTools.getEmail(principal_from);
        if (emailfrom == null)
            throw new Exception(String.format("can't get email information from: %s",
                principal_from.toString()));
        final X500Principal principal_to = CertTools.getSubject(cert);
        final String emailto = CertTools.getEmail(principal_to);
        if (emailto == null)
            throw new Exception(String.format("can't get email information from: %s",
                principal_to.toString()));

        final SMIMESignedGenerator generator_sign = new SMIMESignedGenerator();
        generator_sign.addSigner(privateKey, (X509Certificate) certchain[0],
            SMIMESignedGenerator.DIGEST_SHA1);

        final SMIMEEnvelopedGenerator generator_crypt = new SMIMEEnvelopedGenerator();
        generator_crypt.addKeyTransRecipient(cert);

        final List<Certificate> arraylist = Arrays.asList(certchain);
        final CertStore certstore = CertStore.getInstance("Collection",
            new CollectionCertStoreParameters(arraylist));
        generator_sign.addCertificatesAndCRLs(certstore);

        final MimeBodyPart mimebodypart = new MimeBodyPart();
        mimebodypart.setText("hello toto");
        final MimeMultipart mimemultipart = generator_sign.generate(mimebodypart, "BC");

        final Properties properties = System.getProperties();
        final Session session = Session.getDefaultInstance(properties, null);
        final MimeMessage mimemessage = new MimeMessage(session);
        mimemessage.setContent(mimemultipart, mimemultipart.getContentType());
        mimemessage.saveChanges();

        final MimeBodyPart mimebodypart1 = generator_crypt.generate(mimemessage,
            SMIMEEnvelopedGenerator.AES192_CBC, "BC");

        final MimeMessage mimemessage2 = new MimeMessage(session);
        mimemessage2.setFrom(new InternetAddress(emailfrom));
        mimemessage2.setRecipient(Message.RecipientType.TO, new InternetAddress(emailto));
        mimemessage2.setSubject("example signed and encrypted message");
        mimemessage2.setContent(mimebodypart1.getContent(), mimebodypart1.getContentType());
        mimemessage2.saveChanges();
        mimemessage2.writeTo(new FileOutputStream("/tmp/signed_encrypted.eml"));
    }
}
