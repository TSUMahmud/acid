package nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.configuration;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;
import nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.file.FilePutRequest;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class ConfigurationPutRequest extends FilePutRequest {
    private static HashMap<Short, Class<? extends ConfigItem>> itemsById = new HashMap<>();

    public static abstract class ConfigItem {
        public abstract byte[] getContent();

        public abstract short getId();

        public abstract int getItemSize();

        public abstract void parseData(byte[] bArr);
    }

    static {
        itemsById.put((short) 3, DailyStepGoalConfigItem.class);
        itemsById.put((short) 10, VibrationStrengthConfigItem.class);
        itemsById.put((short) 2, CurrentStepCountConfigItem.class);
        itemsById.put((short) 3, DailyStepGoalConfigItem.class);
        itemsById.put((short) 12, TimeConfigItem.class);
    }

    static ConfigItem[] parsePayload(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        ArrayList<ConfigItem> configItems = new ArrayList<>();
        while (buffer.hasRemaining()) {
            short id = buffer.getShort();
            int length = buffer.get();
            byte[] payload = new byte[length];
            for (int i = 0; i < length; i++) {
                payload[i] = buffer.get();
            }
            Class<? extends ConfigItem> configClass = itemsById.get(Short.valueOf(id));
            if (configClass != null) {
                try {
                    ConfigItem item = (ConfigItem) configClass.newInstance();
                    item.parseData(payload);
                    configItems.add(item);
                } catch (IllegalAccessException | InstantiationException e) {
                    C1238GB.log("error", 3, e);
                }
            }
        }
        return (ConfigItem[]) configItems.toArray(new ConfigItem[0]);
    }

    public ConfigurationPutRequest(ConfigItem item, FossilWatchAdapter adapter) {
        super(2048, createFileContent(new ConfigItem[]{item}), adapter);
    }

    public ConfigurationPutRequest(ConfigItem[] items, FossilWatchAdapter adapter) {
        super(2048, createFileContent(items), adapter);
    }

    private static byte[] createFileContent(ConfigItem[] items) {
        int overallSize = 0;
        for (ConfigItem item : items) {
            overallSize += item.getItemSize() + 3;
        }
        ByteBuffer buffer = ByteBuffer.allocate(overallSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (ConfigItem item2 : items) {
            buffer.putShort(item2.getId());
            buffer.put((byte) item2.getItemSize());
            buffer.put(item2.getContent());
        }
        return buffer.array();
    }

    public static class GenericConfigItem<T> extends ConfigItem {
        private short configId;
        private T value;

        public GenericConfigItem(short configId2, T value2) {
            this.value = value2;
            this.configId = configId2;
        }

        public T getValue() {
            return this.value;
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int getItemSize() {
            /*
                r5 = this;
                T r0 = r5.value
                java.lang.Class r0 = r0.getClass()
                java.lang.String r0 = r0.getName()
                int r1 = r0.hashCode()
                r2 = 3
                r3 = 2
                r4 = 1
                switch(r1) {
                    case -2056817302: goto L_0x0033;
                    case -515992664: goto L_0x0029;
                    case 398507100: goto L_0x001f;
                    case 398795216: goto L_0x0015;
                    default: goto L_0x0014;
                }
            L_0x0014:
                goto L_0x003d
            L_0x0015:
                java.lang.String r1 = "java.lang.Long"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0014
                r0 = 3
                goto L_0x003e
            L_0x001f:
                java.lang.String r1 = "java.lang.Byte"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0014
                r0 = 0
                goto L_0x003e
            L_0x0029:
                java.lang.String r1 = "java.lang.Short"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0014
                r0 = 1
                goto L_0x003e
            L_0x0033:
                java.lang.String r1 = "java.lang.Integer"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x0014
                r0 = 2
                goto L_0x003e
            L_0x003d:
                r0 = -1
            L_0x003e:
                if (r0 == 0) goto L_0x0072
                if (r0 == r4) goto L_0x0071
                if (r0 == r3) goto L_0x006f
                if (r0 != r2) goto L_0x0049
                r0 = 8
                return r0
            L_0x0049:
                java.lang.UnsupportedOperationException r0 = new java.lang.UnsupportedOperationException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "config type "
                r1.append(r2)
                T r2 = r5.value
                java.lang.Class r2 = r2.getClass()
                java.lang.String r2 = r2.getName()
                r1.append(r2)
                java.lang.String r2 = " not supported"
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            L_0x006f:
                r0 = 4
                return r0
            L_0x0071:
                return r3
            L_0x0072:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.configuration.ConfigurationPutRequest.GenericConfigItem.getItemSize():int");
        }

        public short getId() {
            return this.configId;
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public byte[] getContent() {
            /*
                r6 = this;
                int r0 = r6.getItemSize()
                java.nio.ByteBuffer r0 = java.nio.ByteBuffer.allocate(r0)
                java.nio.ByteOrder r1 = java.nio.ByteOrder.LITTLE_ENDIAN
                r0.order(r1)
                T r1 = r6.value
                java.lang.Class r1 = r1.getClass()
                java.lang.String r1 = r1.getName()
                int r2 = r1.hashCode()
                r3 = 3
                r4 = 2
                r5 = 1
                switch(r2) {
                    case -2056817302: goto L_0x0040;
                    case -515992664: goto L_0x0036;
                    case 398507100: goto L_0x002c;
                    case 398795216: goto L_0x0022;
                    default: goto L_0x0021;
                }
            L_0x0021:
                goto L_0x004a
            L_0x0022:
                java.lang.String r2 = "java.lang.Long"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = 2
                goto L_0x004b
            L_0x002c:
                java.lang.String r2 = "java.lang.Byte"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = 0
                goto L_0x004b
            L_0x0036:
                java.lang.String r2 = "java.lang.Short"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = 3
                goto L_0x004b
            L_0x0040:
                java.lang.String r2 = "java.lang.Integer"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = 1
                goto L_0x004b
            L_0x004a:
                r1 = -1
            L_0x004b:
                if (r1 == 0) goto L_0x0078
                if (r1 == r5) goto L_0x006c
                if (r1 == r4) goto L_0x0060
                if (r1 == r3) goto L_0x0054
                goto L_0x0084
            L_0x0054:
                T r1 = r6.value
                java.lang.Short r1 = (java.lang.Short) r1
                short r1 = r1.shortValue()
                r0.putShort(r1)
                goto L_0x0084
            L_0x0060:
                T r1 = r6.value
                java.lang.Long r1 = (java.lang.Long) r1
                long r1 = r1.longValue()
                r0.putLong(r1)
                goto L_0x0084
            L_0x006c:
                T r1 = r6.value
                java.lang.Integer r1 = (java.lang.Integer) r1
                int r1 = r1.intValue()
                r0.putInt(r1)
                goto L_0x0084
            L_0x0078:
                T r1 = r6.value
                java.lang.Byte r1 = (java.lang.Byte) r1
                byte r1 = r1.byteValue()
                r0.put(r1)
            L_0x0084:
                byte[] r1 = r0.array()
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: nodomain.freeyourgadget.gadgetbridge.service.devices.qhybrid.requests.fossil.configuration.ConfigurationPutRequest.GenericConfigItem.getContent():byte[]");
        }

        public void parseData(byte[] data) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            int length = data.length;
            if (length == 1) {
                this.value = Byte.valueOf(buffer.get());
            } else if (length == 2) {
                this.value = Short.valueOf(buffer.getShort());
            } else if (length == 4) {
                this.value = Integer.valueOf(buffer.getInt());
            } else if (length == 8) {
                this.value = Long.valueOf(buffer.getLong());
            }
        }
    }

    public static class DailyStepGoalConfigItem extends GenericConfigItem<Integer> {
        public DailyStepGoalConfigItem() {
            this(-1);
        }

        public DailyStepGoalConfigItem(int value) {
            super(3, Integer.valueOf(value));
        }
    }

    public static class TimezoneOffsetConfigItem extends GenericConfigItem<Short> {
        public TimezoneOffsetConfigItem(Short value) {
            super(17, value);
        }
    }

    public static class VibrationStrengthConfigItem extends GenericConfigItem<Byte> {
        public VibrationStrengthConfigItem() {
            this((byte) -1);
        }

        public VibrationStrengthConfigItem(Byte value) {
            super(10, value);
        }
    }

    public static class CurrentStepCountConfigItem extends GenericConfigItem<Integer> {
        public CurrentStepCountConfigItem() {
            this(-1);
        }

        public CurrentStepCountConfigItem(Integer value) {
            super(2, value);
        }
    }

    public static class TimeConfigItem extends ConfigItem {
        private int epochSeconds;
        private short millis;
        private short offsetMinutes;

        public TimeConfigItem() {
            this(-1, -1, -1);
        }

        public TimeConfigItem(int epochSeconds2, short millis2, short offsetMinutes2) {
            this.epochSeconds = epochSeconds2;
            this.millis = millis2;
            this.offsetMinutes = offsetMinutes2;
        }

        public int getItemSize() {
            return 8;
        }

        public short getId() {
            return 12;
        }

        public byte[] getContent() {
            ByteBuffer buffer = ByteBuffer.allocate(getItemSize());
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(this.epochSeconds);
            buffer.putShort(this.millis);
            buffer.putShort(this.offsetMinutes);
            return buffer.array();
        }

        public void parseData(byte[] data) {
            if (data.length == 8) {
                ByteBuffer buffer = ByteBuffer.wrap(data);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                this.epochSeconds = buffer.getInt();
                this.millis = buffer.getShort();
                this.offsetMinutes = buffer.getShort();
                return;
            }
            throw new RuntimeException("wrong data");
        }
    }
}
