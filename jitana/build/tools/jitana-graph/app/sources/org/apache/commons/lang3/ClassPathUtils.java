package org.apache.commons.lang3;

import p005ch.qos.logback.core.CoreConstants;

public class ClassPathUtils {
    public static String toFullyQualifiedName(Class<?> context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", CoreConstants.CONTEXT_SCOPE_VALUE);
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        return toFullyQualifiedName(context.getPackage(), resourceName);
    }

    public static String toFullyQualifiedName(Package context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", CoreConstants.CONTEXT_SCOPE_VALUE);
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        return context.getName() + "." + resourceName;
    }

    public static String toFullyQualifiedPath(Class<?> context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", CoreConstants.CONTEXT_SCOPE_VALUE);
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        return toFullyQualifiedPath(context.getPackage(), resourceName);
    }

    public static String toFullyQualifiedPath(Package context, String resourceName) {
        Validate.notNull(context, "Parameter '%s' must not be null!", CoreConstants.CONTEXT_SCOPE_VALUE);
        Validate.notNull(resourceName, "Parameter '%s' must not be null!", "resourceName");
        return context.getName().replace('.', '/') + "/" + resourceName;
    }
}
