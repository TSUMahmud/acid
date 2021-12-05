package org.apache.commons.lang3.reflect;

import org.apache.commons.lang3.BooleanUtils;

public class InheritanceUtils {
    public static int distance(Class<?> child, Class<?> parent) {
        if (child == null || parent == null) {
            return -1;
        }
        if (child.equals(parent)) {
            return 0;
        }
        Class<? super Object> superclass = child.getSuperclass();
        int d = BooleanUtils.toInteger(parent.equals(superclass));
        if (d == 1) {
            return d;
        }
        int d2 = d + distance(superclass, parent);
        if (d2 > 0) {
            return d2 + 1;
        }
        return -1;
    }
}
