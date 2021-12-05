package org.apache.commons.lang3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import nodomain.freeyourgadget.gadgetbridge.devices.miband.MiBandConst;

public class SerializationUtils {
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0027, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0030, code lost:
        throw r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T extends java.io.Serializable> T clone(T r6) {
        /*
            if (r6 != 0) goto L_0x0004
            r0 = 0
            return r0
        L_0x0004:
            byte[] r0 = serialize(r6)
            java.io.ByteArrayInputStream r1 = new java.io.ByteArrayInputStream
            r1.<init>(r0)
            org.apache.commons.lang3.SerializationUtils$ClassLoaderAwareObjectInputStream r2 = new org.apache.commons.lang3.SerializationUtils$ClassLoaderAwareObjectInputStream     // Catch:{ ClassNotFoundException -> 0x003a, IOException -> 0x0031 }
            java.lang.Class r3 = r6.getClass()     // Catch:{ ClassNotFoundException -> 0x003a, IOException -> 0x0031 }
            java.lang.ClassLoader r3 = r3.getClassLoader()     // Catch:{ ClassNotFoundException -> 0x003a, IOException -> 0x0031 }
            r2.<init>(r1, r3)     // Catch:{ ClassNotFoundException -> 0x003a, IOException -> 0x0031 }
            java.lang.Object r3 = r2.readObject()     // Catch:{ all -> 0x0025 }
            java.io.Serializable r3 = (java.io.Serializable) r3     // Catch:{ all -> 0x0025 }
            r2.close()     // Catch:{ ClassNotFoundException -> 0x003a, IOException -> 0x0031 }
            return r3
        L_0x0025:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x0027 }
        L_0x0027:
            r4 = move-exception
            r2.close()     // Catch:{ all -> 0x002c }
            goto L_0x0030
        L_0x002c:
            r5 = move-exception
            r3.addSuppressed(r5)     // Catch:{ ClassNotFoundException -> 0x003a, IOException -> 0x0031 }
        L_0x0030:
            throw r4     // Catch:{ ClassNotFoundException -> 0x003a, IOException -> 0x0031 }
        L_0x0031:
            r2 = move-exception
            org.apache.commons.lang3.SerializationException r3 = new org.apache.commons.lang3.SerializationException
            java.lang.String r4 = "IOException while reading or closing cloned object data"
            r3.<init>(r4, r2)
            throw r3
        L_0x003a:
            r2 = move-exception
            org.apache.commons.lang3.SerializationException r3 = new org.apache.commons.lang3.SerializationException
            java.lang.String r4 = "ClassNotFoundException while reading cloned object data"
            r3.<init>(r4, r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.SerializationUtils.clone(java.io.Serializable):java.io.Serializable");
    }

    public static <T extends Serializable> T roundtrip(T msg) {
        return (Serializable) deserialize(serialize(msg));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x001c, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0025, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void serialize(java.io.Serializable r4, java.io.OutputStream r5) {
        /*
            r0 = 0
            if (r5 == 0) goto L_0x0005
            r1 = 1
            goto L_0x0006
        L_0x0005:
            r1 = 0
        L_0x0006:
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r2 = "The OutputStream must not be null"
            org.apache.commons.lang3.Validate.isTrue((boolean) r1, (java.lang.String) r2, (java.lang.Object[]) r0)
            java.io.ObjectOutputStream r0 = new java.io.ObjectOutputStream     // Catch:{ IOException -> 0x0026 }
            r0.<init>(r5)     // Catch:{ IOException -> 0x0026 }
            r0.writeObject(r4)     // Catch:{ all -> 0x001a }
            r0.close()     // Catch:{ IOException -> 0x0026 }
            return
        L_0x001a:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001c }
        L_0x001c:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0021 }
            goto L_0x0025
        L_0x0021:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ IOException -> 0x0026 }
        L_0x0025:
            throw r2     // Catch:{ IOException -> 0x0026 }
        L_0x0026:
            r0 = move-exception
            org.apache.commons.lang3.SerializationException r1 = new org.apache.commons.lang3.SerializationException
            r1.<init>((java.lang.Throwable) r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.SerializationUtils.serialize(java.io.Serializable, java.io.OutputStream):void");
    }

    public static byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, baos);
        return baos.toByteArray();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x001d, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0026, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static <T> T deserialize(java.io.InputStream r4) {
        /*
            r0 = 0
            if (r4 == 0) goto L_0x0005
            r1 = 1
            goto L_0x0006
        L_0x0005:
            r1 = 0
        L_0x0006:
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r2 = "The InputStream must not be null"
            org.apache.commons.lang3.Validate.isTrue((boolean) r1, (java.lang.String) r2, (java.lang.Object[]) r0)
            java.io.ObjectInputStream r0 = new java.io.ObjectInputStream     // Catch:{ ClassNotFoundException -> 0x0029, IOException -> 0x0027 }
            r0.<init>(r4)     // Catch:{ ClassNotFoundException -> 0x0029, IOException -> 0x0027 }
            java.lang.Object r1 = r0.readObject()     // Catch:{ all -> 0x001b }
            r0.close()     // Catch:{ ClassNotFoundException -> 0x0029, IOException -> 0x0027 }
            return r1
        L_0x001b:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x001d }
        L_0x001d:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0022 }
            goto L_0x0026
        L_0x0022:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ ClassNotFoundException -> 0x0029, IOException -> 0x0027 }
        L_0x0026:
            throw r2     // Catch:{ ClassNotFoundException -> 0x0029, IOException -> 0x0027 }
        L_0x0027:
            r0 = move-exception
            goto L_0x002a
        L_0x0029:
            r0 = move-exception
        L_0x002a:
            org.apache.commons.lang3.SerializationException r1 = new org.apache.commons.lang3.SerializationException
            r1.<init>((java.lang.Throwable) r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.SerializationUtils.deserialize(java.io.InputStream):java.lang.Object");
    }

    public static <T> T deserialize(byte[] objectData) {
        Validate.isTrue(objectData != null, "The byte[] must not be null", new Object[0]);
        return deserialize((InputStream) new ByteArrayInputStream(objectData));
    }

    static class ClassLoaderAwareObjectInputStream extends ObjectInputStream {
        private static final Map<String, Class<?>> primitiveTypes = new HashMap();
        private final ClassLoader classLoader;

        static {
            primitiveTypes.put("byte", Byte.TYPE);
            primitiveTypes.put(MiBandConst.DEFAULT_VALUE_VIBRATION_PROFILE, Short.TYPE);
            primitiveTypes.put("int", Integer.TYPE);
            primitiveTypes.put("long", Long.TYPE);
            primitiveTypes.put("float", Float.TYPE);
            primitiveTypes.put("double", Double.TYPE);
            primitiveTypes.put("boolean", Boolean.TYPE);
            primitiveTypes.put("char", Character.TYPE);
            primitiveTypes.put("void", Void.TYPE);
        }

        ClassLoaderAwareObjectInputStream(InputStream in, ClassLoader classLoader2) throws IOException {
            super(in);
            this.classLoader = classLoader2;
        }

        /* access modifiers changed from: protected */
        public Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();
            try {
                return Class.forName(name, false, this.classLoader);
            } catch (ClassNotFoundException e) {
                try {
                    return Class.forName(name, false, Thread.currentThread().getContextClassLoader());
                } catch (ClassNotFoundException cnfe) {
                    Class<?> cls = primitiveTypes.get(name);
                    if (cls != null) {
                        return cls;
                    }
                    throw cnfe;
                }
            }
        }
    }
}
