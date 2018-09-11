package cc.viridian.servicebatchconverter.hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This
 */
public class HashCode {
    public static String getCodigoHash(final String path)
        throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        File f = new File(path);
        InputStream is = new FileInputStream(f);
        byte[] buffer = new byte[(int) f.length()];
        int read = 0;
        while ((read = is.read(buffer)) > 0) {
            digest.update(buffer, 0, read);
        }
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        String output = bigInt.toString(16);
        is.close();
        return output;
    }

    public static boolean areEqualsFileAndHash(final String file, final String hashCode) throws
        NoSuchAlgorithmException, FileNotFoundException, IOException {
        return hashCode.equals(getCodigoHash(file));
    }

    public static boolean compareFileWithFile(final String file1, final String file2) throws
        NoSuchAlgorithmException, FileNotFoundException, IOException {
        return getCodigoHash(file1).equals(getCodigoHash(file2));
    }
}
