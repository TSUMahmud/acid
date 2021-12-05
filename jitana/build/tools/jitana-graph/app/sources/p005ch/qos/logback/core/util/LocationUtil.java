package p005ch.qos.logback.core.util;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

/* renamed from: ch.qos.logback.core.util.LocationUtil */
public class LocationUtil {
    public static final String CLASSPATH_SCHEME = "classpath:";
    public static final String SCHEME_PATTERN = "^\\p{Alpha}[\\p{Alnum}+.-]*:.*$";

    public static URL urlForResource(String str) throws MalformedURLException, FileNotFoundException {
        URL url;
        if (str != null) {
            if (!str.matches(SCHEME_PATTERN)) {
                url = Loader.getResourceBySelfClassLoader(str);
            } else if (str.startsWith(CLASSPATH_SCHEME)) {
                String substring = str.substring(CLASSPATH_SCHEME.length());
                if (substring.startsWith("/")) {
                    substring = substring.substring(1);
                }
                if (substring.length() != 0) {
                    url = Loader.getResourceBySelfClassLoader(substring);
                } else {
                    throw new MalformedURLException("path is required");
                }
            } else {
                url = new URL(str);
            }
            if (url != null) {
                return url;
            }
            throw new FileNotFoundException(str);
        }
        throw new NullPointerException("location is required");
    }
}
