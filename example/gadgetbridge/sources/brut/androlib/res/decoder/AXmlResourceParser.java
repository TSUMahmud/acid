package brut.androlib.res.decoder;

import android.content.res.XmlResourceParser;
import android.util.TypedValue;
import brut.androlib.AndrolibException;
import brut.androlib.res.xml.ResXmlEncoders;
import brut.util.ExtDataInput;
import com.mindprod.ledatastream.LEDataInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmlpull.v1.XmlPullParserException;

public class AXmlResourceParser implements XmlResourceParser {
    private static final int ATTRIBUTE_IX_NAME = 1;
    private static final int ATTRIBUTE_IX_NAMESPACE_URI = 0;
    private static final int ATTRIBUTE_IX_VALUE_DATA = 4;
    private static final int ATTRIBUTE_IX_VALUE_STRING = 2;
    private static final int ATTRIBUTE_IX_VALUE_TYPE = 3;
    private static final int ATTRIBUTE_LENGHT = 5;
    private static final int CHUNK_AXML_FILE = 524291;
    private static final int CHUNK_RESOURCEIDS = 524672;
    private static final int CHUNK_XML_END_NAMESPACE = 1048833;
    private static final int CHUNK_XML_END_TAG = 1048835;
    private static final int CHUNK_XML_FIRST = 1048832;
    private static final int CHUNK_XML_LAST = 1048836;
    private static final int CHUNK_XML_START_NAMESPACE = 1048832;
    private static final int CHUNK_XML_START_TAG = 1048834;
    private static final int CHUNK_XML_TEXT = 1048836;
    private static final String E_NOT_SUPPORTED = "Method is not supported.";
    private static final Logger LOGGER = Logger.getLogger(AXmlResourceParser.class.getName());
    private ResAttrDecoder mAttrDecoder;
    private AndrolibException mFirstError;
    private int[] m_attributes;
    private int m_classAttribute;
    private boolean m_decreaseDepth;
    private int m_event;
    private int m_idAttribute;
    private int m_lineNumber;
    private int m_name;
    private int m_namespaceUri;
    private NamespaceStack m_namespaces;
    private boolean m_operational;
    private ExtDataInput m_reader;
    private int[] m_resourceIDs;
    private StringBlock m_strings;
    private int m_styleAttribute;

    private static final class NamespaceStack {
        private int m_count;
        private int[] m_data = new int[32];
        private int m_dataLength;
        private int m_depth;

        private void ensureDataCapacity(int i) {
            int[] iArr = this.m_data;
            int length = iArr.length;
            int i2 = this.m_dataLength;
            int i3 = length - i2;
            if (i3 <= i) {
                int[] iArr2 = new int[((iArr.length + i3) * 2)];
                System.arraycopy(iArr, 0, iArr2, 0, i2);
                this.m_data = iArr2;
            }
        }

        private final int find(int i, boolean z) {
            int i2 = this.m_dataLength;
            if (i2 == 0) {
                return -1;
            }
            int i3 = i2 - 1;
            for (int i4 = this.m_depth; i4 != 0; i4--) {
                i3 -= 2;
                for (int i5 = this.m_data[i3]; i5 != 0; i5--) {
                    int[] iArr = this.m_data;
                    if (z) {
                        if (iArr[i3] == i) {
                            return iArr[i3 + 1];
                        }
                    } else if (iArr[i3 + 1] == i) {
                        return iArr[i3];
                    }
                    i3 -= 2;
                }
            }
            return -1;
        }

        private final int get(int i, boolean z) {
            if (this.m_dataLength != 0 && i >= 0) {
                int i2 = 0;
                int i3 = this.m_depth;
                while (i3 != 0) {
                    int i4 = this.m_data[i2];
                    if (i >= i4) {
                        i -= i4;
                        i2 += (i4 * 2) + 2;
                        i3--;
                    } else {
                        int i5 = i2 + (i * 2) + 1;
                        if (!z) {
                            i5++;
                        }
                        return this.m_data[i5];
                    }
                }
            }
            return -1;
        }

        public final void decreaseDepth() {
            int i = this.m_dataLength;
            if (i != 0) {
                int i2 = i - 1;
                int i3 = this.m_data[i2];
                int i4 = i3 * 2;
                if ((i2 - 1) - i4 != 0) {
                    this.m_dataLength = i - (i4 + 2);
                    this.m_count -= i3;
                    this.m_depth--;
                }
            }
        }

        public final int findPrefix(int i) {
            return find(i, false);
        }

        public final int findUri(int i) {
            return find(i, true);
        }

        public final int getAccumulatedCount(int i) {
            int i2 = 0;
            if (this.m_dataLength != 0 && i >= 0) {
                int i3 = this.m_depth;
                if (i > i3) {
                    i = i3;
                }
                int i4 = 0;
                while (i != 0) {
                    int i5 = this.m_data[i4];
                    i2 += i5;
                    i4 += (i5 * 2) + 2;
                    i--;
                }
            }
            return i2;
        }

        public final int getCurrentCount() {
            int i = this.m_dataLength;
            if (i == 0) {
                return 0;
            }
            return this.m_data[i - 1];
        }

        public final int getDepth() {
            return this.m_depth;
        }

        public final int getPrefix(int i) {
            return get(i, true);
        }

        public final int getTotalCount() {
            return this.m_count;
        }

        public final int getUri(int i) {
            return get(i, false);
        }

        public final void increaseDepth() {
            ensureDataCapacity(2);
            int i = this.m_dataLength;
            int[] iArr = this.m_data;
            iArr[i] = 0;
            iArr[i + 1] = 0;
            this.m_dataLength = i + 2;
            this.m_depth++;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
            r2 = r0 - 1;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final boolean pop() {
            /*
                r6 = this;
                int r0 = r6.m_dataLength
                r1 = 0
                if (r0 != 0) goto L_0x0006
                return r1
            L_0x0006:
                int r2 = r0 + -1
                int[] r3 = r6.m_data
                r4 = r3[r2]
                if (r4 != 0) goto L_0x000f
                return r1
            L_0x000f:
                int r4 = r4 + -1
                int r2 = r2 + -2
                r3[r2] = r4
                int r1 = r4 * 2
                r5 = 1
                int r1 = r1 + r5
                int r2 = r2 - r1
                r3[r2] = r4
                int r0 = r0 + -2
                r6.m_dataLength = r0
                int r0 = r6.m_count
                int r0 = r0 - r5
                r6.m_count = r0
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: brut.androlib.res.decoder.AXmlResourceParser.NamespaceStack.pop():boolean");
        }

        public final boolean pop(int i, int i2) {
            int i3 = this.m_dataLength;
            if (i3 == 0) {
                return false;
            }
            int i4 = i3 - 1;
            int i5 = this.m_data[i4];
            int i6 = i4 - 2;
            int i7 = 0;
            while (i7 != i5) {
                int[] iArr = this.m_data;
                if (iArr[i6] == i && iArr[i6 + 1] == i2) {
                    int i8 = i5 - 1;
                    if (i7 == 0) {
                        iArr[i6] = i8;
                        iArr[i6 - ((i8 * 2) + 1)] = i8;
                    } else {
                        iArr[i4] = i8;
                        iArr[i4 - ((i8 * 2) + 3)] = i8;
                        System.arraycopy(iArr, i6 + 2, iArr, i6, this.m_dataLength - i6);
                    }
                    this.m_dataLength -= 2;
                    this.m_count--;
                    return true;
                }
                i7++;
                i6 -= 2;
            }
            return false;
        }

        public final void push(int i, int i2) {
            if (this.m_depth == 0) {
                increaseDepth();
            }
            ensureDataCapacity(2);
            int i3 = this.m_dataLength;
            int i4 = i3 - 1;
            int[] iArr = this.m_data;
            int i5 = iArr[i4];
            int i6 = i5 + 1;
            iArr[(i4 - 1) - (i5 * 2)] = i6;
            iArr[i4] = i;
            iArr[i4 + 1] = i2;
            iArr[i4 + 2] = i6;
            this.m_dataLength = i3 + 2;
            this.m_count++;
        }

        public final void reset() {
            this.m_dataLength = 0;
            this.m_count = 0;
            this.m_depth = 0;
        }
    }

    public AXmlResourceParser() {
        this.mAttrDecoder = new ResAttrDecoder();
        this.m_operational = false;
        this.m_namespaces = new NamespaceStack();
        resetEventInfo();
    }

    public AXmlResourceParser(InputStream inputStream) {
        this();
        open(inputStream);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:59:0x018e, code lost:
        throw new java.io.IOException("Invalid chunk type (" + r5 + ").");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void doNext() throws java.io.IOException {
        /*
            r10 = this;
            brut.androlib.res.decoder.StringBlock r0 = r10.m_strings
            r1 = 1
            if (r0 != 0) goto L_0x0021
            brut.util.ExtDataInput r0 = r10.m_reader
            r2 = 524291(0x80003, float:7.34688E-40)
            r0.skipCheckInt(r2)
            brut.util.ExtDataInput r0 = r10.m_reader
            r0.skipInt()
            brut.util.ExtDataInput r0 = r10.m_reader
            brut.androlib.res.decoder.StringBlock r0 = brut.androlib.res.decoder.StringBlock.read(r0)
            r10.m_strings = r0
            brut.androlib.res.decoder.AXmlResourceParser$NamespaceStack r0 = r10.m_namespaces
            r0.increaseDepth()
            r10.m_operational = r1
        L_0x0021:
            int r0 = r10.m_event
            if (r0 != r1) goto L_0x0026
            return
        L_0x0026:
            r10.resetEventInfo()
        L_0x0029:
            boolean r2 = r10.m_decreaseDepth
            r3 = 0
            if (r2 == 0) goto L_0x0035
            r10.m_decreaseDepth = r3
            brut.androlib.res.decoder.AXmlResourceParser$NamespaceStack r2 = r10.m_namespaces
            r2.decreaseDepth()
        L_0x0035:
            r2 = 3
            if (r0 != r2) goto L_0x004c
            brut.androlib.res.decoder.AXmlResourceParser$NamespaceStack r4 = r10.m_namespaces
            int r4 = r4.getDepth()
            if (r4 != r1) goto L_0x004c
            brut.androlib.res.decoder.AXmlResourceParser$NamespaceStack r4 = r10.m_namespaces
            int r4 = r4.getCurrentCount()
            if (r4 != 0) goto L_0x004c
            r10.m_event = r1
            goto L_0x014f
        L_0x004c:
            r4 = 1048834(0x100102, float:1.46973E-39)
            if (r0 != 0) goto L_0x0055
            r5 = 1048834(0x100102, float:1.46973E-39)
            goto L_0x005b
        L_0x0055:
            brut.util.ExtDataInput r5 = r10.m_reader
            int r5 = r5.readInt()
        L_0x005b:
            r6 = 524672(0x80180, float:7.35222E-40)
            r7 = 2
            java.lang.String r8 = ")."
            if (r5 != r6) goto L_0x0097
            brut.util.ExtDataInput r2 = r10.m_reader
            int r2 = r2.readInt()
            r3 = 8
            if (r2 < r3) goto L_0x007d
            int r3 = r2 % 4
            if (r3 != 0) goto L_0x007d
            brut.util.ExtDataInput r3 = r10.m_reader
            int r2 = r2 / 4
            int r2 = r2 - r7
            int[] r2 = r3.readIntArray(r2)
            r10.m_resourceIDs = r2
            goto L_0x0029
        L_0x007d:
            java.io.IOException r0 = new java.io.IOException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Invalid resource ids size ("
            r1.append(r3)
            r1.append(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0097:
            r6 = 1048832(0x100100, float:1.469727E-39)
            if (r5 < r6) goto L_0x0174
            r9 = 1048836(0x100104, float:1.469732E-39)
            if (r5 > r9) goto L_0x0174
            if (r5 != r4) goto L_0x00aa
            r8 = -1
            if (r0 != r8) goto L_0x00aa
            r10.m_event = r3
            goto L_0x014f
        L_0x00aa:
            brut.util.ExtDataInput r3 = r10.m_reader
            r3.skipInt()
            brut.util.ExtDataInput r3 = r10.m_reader
            int r3 = r3.readInt()
            brut.util.ExtDataInput r8 = r10.m_reader
            r8.skipInt()
            if (r5 == r6) goto L_0x0150
            r8 = 1048833(0x100101, float:1.469728E-39)
            if (r5 != r8) goto L_0x00c3
            goto L_0x0150
        L_0x00c3:
            r10.m_lineNumber = r3
            if (r5 != r4) goto L_0x011e
            brut.util.ExtDataInput r0 = r10.m_reader
            int r0 = r0.readInt()
            r10.m_namespaceUri = r0
            brut.util.ExtDataInput r0 = r10.m_reader
            int r0 = r0.readInt()
            r10.m_name = r0
            brut.util.ExtDataInput r0 = r10.m_reader
            r0.skipInt()
            brut.util.ExtDataInput r0 = r10.m_reader
            int r0 = r0.readInt()
            int r3 = r0 >>> 16
            int r3 = r3 - r1
            r10.m_idAttribute = r3
            r3 = 65535(0xffff, float:9.1834E-41)
            r0 = r0 & r3
            brut.util.ExtDataInput r4 = r10.m_reader
            int r4 = r4.readInt()
            r10.m_classAttribute = r4
            int r4 = r10.m_classAttribute
            int r5 = r4 >>> 16
            int r5 = r5 - r1
            r10.m_styleAttribute = r5
            r3 = r3 & r4
            int r3 = r3 - r1
            r10.m_classAttribute = r3
            brut.util.ExtDataInput r1 = r10.m_reader
            int r0 = r0 * 5
            int[] r0 = r1.readIntArray(r0)
            r10.m_attributes = r0
        L_0x0108:
            int[] r0 = r10.m_attributes
            int r1 = r0.length
            if (r2 >= r1) goto L_0x0116
            r1 = r0[r2]
            int r1 = r1 >>> 24
            r0[r2] = r1
            int r2 = r2 + 5
            goto L_0x0108
        L_0x0116:
            brut.androlib.res.decoder.AXmlResourceParser$NamespaceStack r0 = r10.m_namespaces
            r0.increaseDepth()
            r10.m_event = r7
            goto L_0x014f
        L_0x011e:
            r3 = 1048835(0x100103, float:1.469731E-39)
            if (r5 != r3) goto L_0x0138
            brut.util.ExtDataInput r0 = r10.m_reader
            int r0 = r0.readInt()
            r10.m_namespaceUri = r0
            brut.util.ExtDataInput r0 = r10.m_reader
            int r0 = r0.readInt()
            r10.m_name = r0
            r10.m_event = r2
            r10.m_decreaseDepth = r1
            goto L_0x014f
        L_0x0138:
            if (r5 != r9) goto L_0x0029
            brut.util.ExtDataInput r0 = r10.m_reader
            int r0 = r0.readInt()
            r10.m_name = r0
            brut.util.ExtDataInput r0 = r10.m_reader
            r0.skipInt()
            brut.util.ExtDataInput r0 = r10.m_reader
            r0.skipInt()
            r0 = 4
            r10.m_event = r0
        L_0x014f:
            return
        L_0x0150:
            brut.util.ExtDataInput r2 = r10.m_reader
            if (r5 != r6) goto L_0x0165
            int r2 = r2.readInt()
            brut.util.ExtDataInput r3 = r10.m_reader
            int r3 = r3.readInt()
            brut.androlib.res.decoder.AXmlResourceParser$NamespaceStack r4 = r10.m_namespaces
            r4.push(r2, r3)
            goto L_0x0029
        L_0x0165:
            r2.skipInt()
            brut.util.ExtDataInput r2 = r10.m_reader
            r2.skipInt()
            brut.androlib.res.decoder.AXmlResourceParser$NamespaceStack r2 = r10.m_namespaces
            r2.pop()
            goto L_0x0029
        L_0x0174:
            java.io.IOException r0 = new java.io.IOException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Invalid chunk type ("
            r1.append(r2)
            r1.append(r5)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            goto L_0x018f
        L_0x018e:
            throw r0
        L_0x018f:
            goto L_0x018e
        */
        throw new UnsupportedOperationException("Method not decompiled: brut.androlib.res.decoder.AXmlResourceParser.doNext():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002f, code lost:
        return r0 / 5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final int findAttribute(java.lang.String r6, java.lang.String r7) {
        /*
            r5 = this;
            brut.androlib.res.decoder.StringBlock r0 = r5.m_strings
            r1 = -1
            if (r0 == 0) goto L_0x0032
            if (r7 != 0) goto L_0x0008
            goto L_0x0032
        L_0x0008:
            int r7 = r0.find(r7)
            if (r7 != r1) goto L_0x000f
            return r1
        L_0x000f:
            if (r6 == 0) goto L_0x0018
            brut.androlib.res.decoder.StringBlock r0 = r5.m_strings
            int r6 = r0.find(r6)
            goto L_0x0019
        L_0x0018:
            r6 = -1
        L_0x0019:
            r0 = 0
        L_0x001a:
            int[] r2 = r5.m_attributes
            int r3 = r2.length
            if (r0 == r3) goto L_0x0032
            int r3 = r0 + 1
            r4 = r2[r3]
            if (r7 != r4) goto L_0x0030
            if (r6 == r1) goto L_0x002d
            int r4 = r0 + 0
            r2 = r2[r4]
            if (r6 != r2) goto L_0x0030
        L_0x002d:
            int r0 = r0 / 5
            return r0
        L_0x0030:
            r0 = r3
            goto L_0x001a
        L_0x0032:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: brut.androlib.res.decoder.AXmlResourceParser.findAttribute(java.lang.String, java.lang.String):int");
    }

    private final int getAttributeOffset(int i) {
        if (this.m_event == 2) {
            int i2 = i * 5;
            if (i2 < this.m_attributes.length) {
                return i2;
            }
            throw new IndexOutOfBoundsException("Invalid attribute index (" + i + ").");
        }
        throw new IndexOutOfBoundsException("Current event is not START_TAG.");
    }

    private final void resetEventInfo() {
        this.m_event = -1;
        this.m_lineNumber = -1;
        this.m_name = -1;
        this.m_namespaceUri = -1;
        this.m_attributes = null;
        this.m_idAttribute = -1;
        this.m_classAttribute = -1;
        this.m_styleAttribute = -1;
    }

    private void setFirstError(AndrolibException androlibException) {
        if (this.mFirstError == null) {
            this.mFirstError = androlibException;
        }
    }

    public void close() {
        if (this.m_operational) {
            this.m_operational = false;
            this.m_reader = null;
            this.m_strings = null;
            this.m_resourceIDs = null;
            this.m_namespaces.reset();
            resetEventInfo();
        }
    }

    public void defineEntityReplacementText(String str, String str2) throws XmlPullParserException {
        throw new XmlPullParserException(E_NOT_SUPPORTED);
    }

    public ResAttrDecoder getAttrDecoder() {
        return this.mAttrDecoder;
    }

    public boolean getAttributeBooleanValue(int i, boolean z) {
        return getAttributeIntValue(i, z ? 1 : 0) != 0;
    }

    public boolean getAttributeBooleanValue(String str, String str2, boolean z) {
        int findAttribute = findAttribute(str, str2);
        return findAttribute == -1 ? z : getAttributeBooleanValue(findAttribute, z);
    }

    public int getAttributeCount() {
        if (this.m_event != 2) {
            return -1;
        }
        return this.m_attributes.length / 5;
    }

    public float getAttributeFloatValue(int i, float f) {
        int attributeOffset = getAttributeOffset(i);
        int[] iArr = this.m_attributes;
        return iArr[attributeOffset + 3] == 4 ? Float.intBitsToFloat(iArr[attributeOffset + 4]) : f;
    }

    public float getAttributeFloatValue(String str, String str2, float f) {
        int findAttribute = findAttribute(str, str2);
        return findAttribute == -1 ? f : getAttributeFloatValue(findAttribute, f);
    }

    public int getAttributeIntValue(int i, int i2) {
        int attributeOffset = getAttributeOffset(i);
        int[] iArr = this.m_attributes;
        int i3 = iArr[attributeOffset + 3];
        return (i3 < 16 || i3 > 31) ? i2 : iArr[attributeOffset + 4];
    }

    public int getAttributeIntValue(String str, String str2, int i) {
        int findAttribute = findAttribute(str, str2);
        return findAttribute == -1 ? i : getAttributeIntValue(findAttribute, i);
    }

    public int getAttributeListValue(int i, String[] strArr, int i2) {
        return 0;
    }

    public int getAttributeListValue(String str, String str2, String[] strArr, int i) {
        return 0;
    }

    public String getAttributeName(int i) {
        int i2 = this.m_attributes[getAttributeOffset(i) + 1];
        return i2 == -1 ? "" : this.m_strings.getString(i2);
    }

    public int getAttributeNameResource(int i) {
        int i2 = this.m_attributes[getAttributeOffset(i) + 1];
        int[] iArr = this.m_resourceIDs;
        if (iArr == null || i2 < 0 || i2 >= iArr.length) {
            return 0;
        }
        return iArr[i2];
    }

    public String getAttributeNamespace(int i) {
        int i2 = this.m_attributes[getAttributeOffset(i) + 0];
        return i2 == -1 ? "" : this.m_strings.getString(i2);
    }

    public String getAttributePrefix(int i) {
        int findPrefix = this.m_namespaces.findPrefix(this.m_attributes[getAttributeOffset(i) + 0]);
        return findPrefix == -1 ? "" : this.m_strings.getString(findPrefix);
    }

    public int getAttributeResourceValue(int i, int i2) {
        int attributeOffset = getAttributeOffset(i);
        int[] iArr = this.m_attributes;
        return iArr[attributeOffset + 3] == 1 ? iArr[attributeOffset + 4] : i2;
    }

    public int getAttributeResourceValue(String str, String str2, int i) {
        int findAttribute = findAttribute(str, str2);
        return findAttribute == -1 ? i : getAttributeResourceValue(findAttribute, i);
    }

    public String getAttributeType(int i) {
        return "CDATA";
    }

    public int getAttributeUnsignedIntValue(int i, int i2) {
        return getAttributeIntValue(i, i2);
    }

    public int getAttributeUnsignedIntValue(String str, String str2, int i) {
        int findAttribute = findAttribute(str, str2);
        return findAttribute == -1 ? i : getAttributeUnsignedIntValue(findAttribute, i);
    }

    public String getAttributeValue(int i) {
        String str;
        int attributeOffset = getAttributeOffset(i);
        int[] iArr = this.m_attributes;
        int i2 = iArr[attributeOffset + 3];
        int i3 = iArr[attributeOffset + 4];
        int i4 = iArr[attributeOffset + 2];
        ResAttrDecoder resAttrDecoder = this.mAttrDecoder;
        if (resAttrDecoder != null) {
            if (i4 == -1) {
                str = null;
            } else {
                try {
                    str = ResXmlEncoders.escapeXmlChars(this.m_strings.getString(i4));
                } catch (AndrolibException e) {
                    setFirstError(e);
                    LOGGER.log(Level.WARNING, String.format("Could not decode attr value, using undecoded value instead: ns=%s, name=%s, value=0x%08x", new Object[]{getAttributePrefix(i), getAttributeName(i), Integer.valueOf(i3)}), e);
                }
            }
            return resAttrDecoder.decode(i2, i3, str, getAttributeNameResource(i));
        }
        return TypedValue.coerceToString(i2, i3);
    }

    public String getAttributeValue(String str, String str2) {
        int findAttribute = findAttribute(str, str2);
        if (findAttribute == -1) {
            return null;
        }
        return getAttributeValue(findAttribute);
    }

    public int getAttributeValueData(int i) {
        return this.m_attributes[getAttributeOffset(i) + 4];
    }

    public int getAttributeValueType(int i) {
        return this.m_attributes[getAttributeOffset(i) + 3];
    }

    public String getClassAttribute() {
        int i = this.m_classAttribute;
        if (i == -1) {
            return null;
        }
        return this.m_strings.getString(this.m_attributes[getAttributeOffset(i) + 2]);
    }

    public int getColumnNumber() {
        return -1;
    }

    public int getDepth() {
        return this.m_namespaces.getDepth() - 1;
    }

    public int getEventType() throws XmlPullParserException {
        return this.m_event;
    }

    public boolean getFeature(String str) {
        return false;
    }

    public AndrolibException getFirstError() {
        return this.mFirstError;
    }

    public String getIdAttribute() {
        int i = this.m_idAttribute;
        if (i == -1) {
            return null;
        }
        return this.m_strings.getString(this.m_attributes[getAttributeOffset(i) + 2]);
    }

    public int getIdAttributeResourceValue(int i) {
        int i2 = this.m_idAttribute;
        if (i2 == -1) {
            return i;
        }
        int attributeOffset = getAttributeOffset(i2);
        int[] iArr = this.m_attributes;
        return iArr[attributeOffset + 3] != 1 ? i : iArr[attributeOffset + 4];
    }

    public String getInputEncoding() {
        return null;
    }

    public int getLineNumber() {
        return this.m_lineNumber;
    }

    public String getName() {
        if (this.m_name == -1) {
            return null;
        }
        int i = this.m_event;
        if (i == 2 || i == 3) {
            return this.m_strings.getString(this.m_name);
        }
        return null;
    }

    public String getNamespace() {
        return this.m_strings.getString(this.m_namespaceUri);
    }

    public String getNamespace(String str) {
        throw new RuntimeException(E_NOT_SUPPORTED);
    }

    public int getNamespaceCount(int i) throws XmlPullParserException {
        return this.m_namespaces.getAccumulatedCount(i);
    }

    public String getNamespacePrefix(int i) throws XmlPullParserException {
        return this.m_strings.getString(this.m_namespaces.getPrefix(i));
    }

    public String getNamespaceUri(int i) throws XmlPullParserException {
        return this.m_strings.getString(this.m_namespaces.getUri(i));
    }

    public String getPositionDescription() {
        return "XML line #" + getLineNumber();
    }

    public String getPrefix() {
        return this.m_strings.getString(this.m_namespaces.findPrefix(this.m_namespaceUri));
    }

    public Object getProperty(String str) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public final StringBlock getStrings() {
        return this.m_strings;
    }

    public int getStyleAttribute() {
        int i = this.m_styleAttribute;
        if (i == -1) {
            return 0;
        }
        return this.m_attributes[getAttributeOffset(i) + 4];
    }

    public String getText() {
        int i = this.m_name;
        if (i == -1 || this.m_event != 4) {
            return null;
        }
        return this.m_strings.getString(i);
    }

    public char[] getTextCharacters(int[] iArr) {
        String text = getText();
        if (text == null) {
            return null;
        }
        iArr[0] = 0;
        iArr[1] = text.length();
        char[] cArr = new char[text.length()];
        text.getChars(0, text.length(), cArr, 0);
        return cArr;
    }

    public boolean isAttributeDefault(int i) {
        return false;
    }

    public boolean isEmptyElementTag() throws XmlPullParserException {
        return false;
    }

    public boolean isWhitespace() throws XmlPullParserException {
        return false;
    }

    public int next() throws XmlPullParserException, IOException {
        if (this.m_reader != null) {
            try {
                doNext();
                return this.m_event;
            } catch (IOException e) {
                close();
                throw e;
            }
        } else {
            throw new XmlPullParserException("Parser is not opened.", this, (Throwable) null);
        }
    }

    public int nextTag() throws XmlPullParserException, IOException {
        int next = next();
        if (next == 4 && isWhitespace()) {
            next = next();
        }
        if (next == 2 || next == 3) {
            return next;
        }
        throw new XmlPullParserException("Expected start or end tag.", this, (Throwable) null);
    }

    public String nextText() throws XmlPullParserException, IOException {
        if (getEventType() == 2) {
            int next = next();
            if (next == 4) {
                String text = getText();
                if (next() == 3) {
                    return text;
                }
                throw new XmlPullParserException("Event TEXT must be immediately followed by END_TAG.", this, (Throwable) null);
            } else if (next == 3) {
                return "";
            } else {
                throw new XmlPullParserException("Parser must be on START_TAG or TEXT to read text.", this, (Throwable) null);
            }
        } else {
            throw new XmlPullParserException("Parser must be on START_TAG to read next text.", this, (Throwable) null);
        }
    }

    public int nextToken() throws XmlPullParserException, IOException {
        return next();
    }

    public void open(InputStream inputStream) {
        close();
        if (inputStream != null) {
            this.m_reader = new ExtDataInput((DataInput) new LEDataInputStream(inputStream));
        }
    }

    public void require(int i, String str, String str2) throws XmlPullParserException, IOException {
        if (i != getEventType() || ((str != null && !str.equals(getNamespace())) || (str2 != null && !str2.equals(getName())))) {
            throw new XmlPullParserException(TYPES[i] + " is expected.", this, (Throwable) null);
        }
    }

    public void setAttrDecoder(ResAttrDecoder resAttrDecoder) {
        this.mAttrDecoder = resAttrDecoder;
    }

    public void setFeature(String str, boolean z) throws XmlPullParserException {
        throw new XmlPullParserException(E_NOT_SUPPORTED);
    }

    public void setInput(InputStream inputStream, String str) throws XmlPullParserException {
        open(inputStream);
    }

    public void setInput(Reader reader) throws XmlPullParserException {
        throw new XmlPullParserException(E_NOT_SUPPORTED);
    }

    public void setProperty(String str, Object obj) throws XmlPullParserException {
        throw new XmlPullParserException(E_NOT_SUPPORTED);
    }
}
