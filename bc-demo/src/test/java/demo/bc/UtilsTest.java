package demo.bc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
    private static final Log log = LogFactory.getLog(UtilsTest.class);

    private static File clearFile = new File("/tmp/clear.txt");
    private static File base64File = new File("/tmp/base64.txt");
    private static String clearString = "abcdefghijklmnopqrstuvwxyz0123456789abcdefghijklmnopqrstuvwxyz0123456789";

    @Test
    public void encode_decode_base64() throws Exception {
        log.debug(null);
        log.info("encode_decode_base64");
        writeClearFile();

        Utils.encodeBase64(clearFile, base64File);
        final String base64String = new String(Utils.encodeBase64_apache(clearString.getBytes()));

        char[] cbuf = readFileChar(base64File);
        log.debug(String.valueOf(cbuf));
        log.debug(String.valueOf(base64String));
        Assert.assertTrue(base64String.equals(String.valueOf(cbuf)));

        byte[] bbuf = readFileByte(base64File);
        bbuf = Utils.decodeBase64_apache(bbuf);
        log.debug(new String(bbuf));
        Assert.assertTrue(clearString.equals(new String(bbuf)));

        Utils.decodeBase64(base64File, clearFile);
        Assert.assertTrue(clearFile.length() == clearString.length());

        cbuf = readFileChar(clearFile);
        log.debug(String.valueOf(cbuf));
        Assert.assertTrue(clearString.equals(String.valueOf(cbuf)));
    }

    @Test
    public void encode_decode_base64_2() throws Exception {
        log.debug(null);
        log.info("encode_decode_base64_2");
        writeClearFile();

        Utils.encodeBase64Chunked(clearFile, base64File);
        final String base64String = new String(Utils.encodeBase64Chunked_apache(clearString
            .getBytes()));

        char[] cbuf = readFileChar(base64File);
        log.debug(String.valueOf(cbuf));
        log.debug(String.valueOf(base64String));
        // Assert.assertTrue(base64String.equals(String.valueOf(cbuf)));

        byte[] bbuf = readFileByte(base64File);
        bbuf = Utils.decodeBase64_apache(bbuf);
        log.debug(new String(bbuf));
        Assert.assertTrue(clearString.equals(new String(bbuf)));

        Utils.decodeBase64(base64File, clearFile);
        Assert.assertTrue(clearFile.length() == clearString.length());

        cbuf = readFileChar(clearFile);
        log.debug(String.valueOf(cbuf));
        Assert.assertTrue(clearString.equals(String.valueOf(cbuf)));
    }

    @Test
    public void encode_decode_base16() throws Exception {
        log.debug(null);
        log.info("encode_decode_base16");
        writeClearFile();

        final String base16String = Utils.encodeHexString_apache(clearString.getBytes());
        final String base16String2 = Utils.encodeHex(clearString);

        log.debug(String.valueOf(clearString));
        log.debug(String.valueOf(base16String));
        log.debug(String.valueOf(base16String2));
        Assert.assertTrue(base16String.equals(base16String2));

        byte[] bbuf = Utils.decodeHex_apache(base16String);
        Assert.assertTrue(clearString.equals(new String(bbuf)));
        bbuf = Utils.decodeHex_apache(base16String2);
        Assert.assertTrue(clearString.equals(new String(bbuf)));

        String sbuf = Utils.decodeHex(base16String);
        Assert.assertTrue(clearString.equals(sbuf));
        sbuf = Utils.decodeHex(base16String2);
        Assert.assertTrue(clearString.equals(sbuf));
    }

    static void writeClearFile() throws Exception {
        final FileWriter fw = new FileWriter(clearFile);
        fw.write(clearString);
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
