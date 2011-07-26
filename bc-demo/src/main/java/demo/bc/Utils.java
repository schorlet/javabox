package demo.bc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public final class Utils {
    static final byte[] line_separator = System.getProperty("line.separator").getBytes();
    static final String digits = "0123456789abcdef";

    private Utils() {}

    // apache hex
    public static char[] encodeHex_apache(final byte[] data) {
        if (data == null) return null;
        return Hex.encodeHex(data);
    }

    public static String encodeHexString_apache(final byte[] data) {
        if (data == null) return null;
        return String.valueOf(encodeHex_apache(data));
    }

    public static byte[] decodeHex_apache(final char[] data) throws Exception {
        if (data == null) return null;
        return Hex.decodeHex(data);
    }

    public static byte[] decodeHex_apache(final String data) throws Exception {
        if (data == null) return null;
        return decodeHex_apache(data.toCharArray());
    }

    // mine hex
    public static String encodeHex(final String s) {
        if (s == null) return null;
        final byte[] data = s.getBytes();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i != data.length; i++) {
            final int v = data[i] & 0xff;

            sb.append(digits.charAt(v >> 4));
            sb.append(digits.charAt(v & 0xf));
        }
        return sb.toString();
    }

    public static String decodeHex(final String s) {
        if (s == null) return null;
        final char[] data = s.toLowerCase().toCharArray();
        final StringBuffer sb = new StringBuffer();

        for (int i = 0; i < data.length;) {
            final int c = digits.indexOf(data[i++] & 0xff) << 4 | digits.indexOf(data[i++] & 0xff);

            if ((c & 0xff) < 0x20 || (c & 0xff) > 0x7e) {
                sb.append('.');
            } else {
                sb.append((char) c);
            }
        }
        return sb.toString();
    }

    // apache base64
    public static byte[] encodeBase64_apache(final byte[] data) {
        if (data == null) return null;
        return Base64.encodeBase64(data);
    }

    public static byte[] encodeBase64Chunked_apache(final byte[] data) {
        if (data == null) return null;
        return Base64.encodeBase64Chunked(data);
    }

    public static byte[] decodeBase64_apache(final byte[] data) {
        if (data == null) return null;
        return Base64.decodeBase64(data);
    }

    // mine base64
    public static void encodeBase64Chunked(final File in, final File out) throws Exception {
        encodeBase64(in, out, true);
    }

    public static void encodeBase64(final File in, final File out) throws Exception {
        encodeBase64(in, out, false);
    }

    public static void encodeBase64(final File in, final File out, final boolean chunked)
        throws Exception {
        final FileInputStream inbinary = new FileInputStream(in);
        final FileOutputStream out64 = new FileOutputStream(out);
        final byte[] buf = new byte[3];
        int i = 0;
        int j = 0;
        while ((i = inbinary.read(buf)) >= 0) {
            final byte[] buf2 = new byte[i];
            System.arraycopy(buf, 0, buf2, 0, i);
            out64.write(Base64.encodeBase64(buf2));
            if (chunked) {
                j = j + 4;
                if (j == 76) {
                    j = 0;
                    out64.write(line_separator);
                }
            }
        }
        inbinary.close();
        if (j != 0) {
            out64.write(line_separator);
        }
        out64.flush();
        out64.close();
    }

    public static void decodeBase64(final File in, final File out) throws Exception {
        final FileInputStream in64 = new FileInputStream(in);
        final FileOutputStream outbinary = new FileOutputStream(out);
        final byte[] buf = new byte[4];
        int i = 0;
        while ((i = in64.read(buf)) >= 0) {
            int k = 0;
            final byte[] buf2 = new byte[4];
            for (int j = 0; j < i; j++) {
                if (buf[j] != (byte) '\r' && buf[j] != (byte) '\n') {
                    buf2[k++] = buf[j];
                }
            }
            while (k < buf2.length && (i = in64.read()) >= 0) {
                if (i != (byte) '\r' && i != (byte) '\n') {
                    buf2[k++] = (byte) i;
                }
            }
            outbinary.write(Base64.decodeBase64(buf2));
        }
        in64.close();
        outbinary.flush();
        outbinary.close();
    }
}
