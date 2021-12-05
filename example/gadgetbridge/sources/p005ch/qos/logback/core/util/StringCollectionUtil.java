package p005ch.qos.logback.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/* renamed from: ch.qos.logback.core.util.StringCollectionUtil */
public class StringCollectionUtil {
    public static void removeMatching(Collection<String> collection, Collection<String> collection2) {
        ArrayList arrayList = new ArrayList(collection.size());
        for (String compile : collection2) {
            Pattern compile2 = Pattern.compile(compile);
            for (String next : collection) {
                if (compile2.matcher(next).matches()) {
                    arrayList.add(next);
                }
            }
        }
        collection.removeAll(arrayList);
    }

    public static void removeMatching(Collection<String> collection, String... strArr) {
        removeMatching(collection, (Collection<String>) Arrays.asList(strArr));
    }

    public static void retainMatching(Collection<String> collection, Collection<String> collection2) {
        if (!collection2.isEmpty()) {
            ArrayList arrayList = new ArrayList(collection.size());
            for (String compile : collection2) {
                Pattern compile2 = Pattern.compile(compile);
                for (String next : collection) {
                    if (compile2.matcher(next).matches()) {
                        arrayList.add(next);
                    }
                }
            }
            collection.retainAll(arrayList);
        }
    }

    public static void retainMatching(Collection<String> collection, String... strArr) {
        retainMatching(collection, (Collection<String>) Arrays.asList(strArr));
    }
}
