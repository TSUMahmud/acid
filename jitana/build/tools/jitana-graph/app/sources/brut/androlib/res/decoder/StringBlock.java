package brut.androlib.res.decoder;

import brut.androlib.res.xml.ResXmlEncoders;
import brut.util.ExtDataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.CharEncoding;
import p005ch.qos.logback.core.CoreConstants;

public class StringBlock {
    private static final int CHUNK_TYPE = 1835009;
    private static final Logger LOGGER = Logger.getLogger(StringBlock.class.getName());
    private static final CharsetDecoder UTF16LE_DECODER = Charset.forName(CharEncoding.UTF_16LE).newDecoder();
    private static final CharsetDecoder UTF8_DECODER = Charset.forName(CharEncoding.UTF_8).newDecoder();
    private static final int UTF8_FLAG = 256;
    private boolean m_isUTF8;
    private int[] m_stringOffsets;
    private byte[] m_strings;
    private int[] m_styleOffsets;
    private int[] m_styles;

    private StringBlock() {
    }

    private String decodeString(int i, int i2) {
        try {
            return (this.m_isUTF8 ? UTF8_DECODER : UTF16LE_DECODER).decode(ByteBuffer.wrap(this.m_strings, i, i2)).toString();
        } catch (CharacterCodingException e) {
            LOGGER.log(Level.WARNING, (String) null, e);
            return null;
        }
    }

    private static final int getShort(byte[] bArr, int i) {
        return (bArr[i] & 255) | ((bArr[i + 1] & 255) << 8);
    }

    private static final int getShort(int[] iArr, int i) {
        int i2 = iArr[i / 4];
        return (i % 4) / 2 == 0 ? i2 & 65535 : i2 >>> 16;
    }

    private int[] getStyle(int i) {
        int[] iArr;
        int[] iArr2 = this.m_styleOffsets;
        int[] iArr3 = null;
        if (iArr2 != null && this.m_styles != null && i < iArr2.length) {
            int i2 = iArr2[i] / 4;
            int i3 = 0;
            int i4 = i2;
            int i5 = 0;
            while (true) {
                int[] iArr4 = this.m_styles;
                if (i4 < iArr4.length && iArr4[i4] != -1) {
                    i5++;
                    i4++;
                } else if (i5 != 0 && i5 % 3 == 0) {
                    iArr3 = new int[i5];
                    while (true) {
                        iArr = this.m_styles;
                        if (i2 >= iArr.length || iArr[i2] == -1) {
                            break;
                        }
                        iArr3[i3] = iArr[i2];
                        i3++;
                        i2++;
                    }
                }
            }
            iArr3 = new int[i5];
            while (true) {
                iArr = this.m_styles;
                iArr3[i3] = iArr[i2];
                i3++;
                i2++;
            }
        }
        return iArr3;
    }

    private static final int[] getVarint(byte[] bArr, int i) {
        byte b = bArr[i];
        boolean z = (b & 128) != 0;
        int i2 = b & Byte.MAX_VALUE;
        if (!z) {
            return new int[]{i2, 1};
        }
        return new int[]{(bArr[i + 1] & 255) | (i2 << 8), 2};
    }

    private void outputStyleTag(String str, StringBuilder sb, boolean z) {
        String str2;
        sb.append('<');
        if (z) {
            sb.append('/');
        }
        int indexOf = str.indexOf(59);
        if (indexOf == -1) {
            sb.append(str);
        } else {
            sb.append(str.substring(0, indexOf));
            if (!z) {
                int i = indexOf;
                boolean z2 = true;
                while (z2) {
                    int i2 = i + 1;
                    int indexOf2 = str.indexOf(61, i2);
                    sb.append(' ');
                    sb.append(str.substring(i2, indexOf2));
                    sb.append("=\"");
                    int i3 = indexOf2 + 1;
                    i = str.indexOf(59, i3);
                    if (i != -1) {
                        str2 = str.substring(i3, i);
                    } else {
                        str2 = str.substring(i3);
                        z2 = false;
                    }
                    sb.append(ResXmlEncoders.escapeXmlChars(str2));
                    sb.append(CoreConstants.DOUBLE_QUOTE_CHAR);
                }
            }
        }
        sb.append('>');
    }

    public static StringBlock read(ExtDataInput extDataInput) throws IOException {
        extDataInput.skipCheckInt(CHUNK_TYPE);
        int readInt = extDataInput.readInt();
        int readInt2 = extDataInput.readInt();
        int readInt3 = extDataInput.readInt();
        int readInt4 = extDataInput.readInt();
        int readInt5 = extDataInput.readInt();
        int readInt6 = extDataInput.readInt();
        StringBlock stringBlock = new StringBlock();
        stringBlock.m_isUTF8 = (readInt4 & 256) != 0;
        stringBlock.m_stringOffsets = extDataInput.readIntArray(readInt2);
        if (readInt3 != 0) {
            stringBlock.m_styleOffsets = extDataInput.readIntArray(readInt3);
        }
        int i = (readInt6 == 0 ? readInt : readInt6) - readInt5;
        if (i % 4 == 0) {
            stringBlock.m_strings = new byte[i];
            extDataInput.readFully(stringBlock.m_strings);
            if (readInt6 != 0) {
                int i2 = readInt - readInt6;
                if (i2 % 4 == 0) {
                    stringBlock.m_styles = extDataInput.readIntArray(i2 / 4);
                } else {
                    throw new IOException("Style data size is not multiple of 4 (" + i2 + ").");
                }
            }
            return stringBlock;
        }
        throw new IOException("String data size is not multiple of 4 (" + i + ").");
    }

    public int find(String str) {
        if (str == null) {
            return -1;
        }
        int i = 0;
        while (true) {
            int[] iArr = this.m_stringOffsets;
            if (i == iArr.length) {
                return -1;
            }
            int i2 = iArr[i];
            int i3 = getShort(this.m_strings, i2);
            if (i3 == str.length()) {
                int i4 = i2;
                int i5 = 0;
                while (i5 != i3) {
                    i4 += 2;
                    if (str.charAt(i5) != getShort(this.m_strings, i4)) {
                        break;
                    }
                    i5++;
                }
                if (i5 == i3) {
                    return i;
                }
            }
            i++;
        }
    }

    public CharSequence get(int i) {
        return getString(i);
    }

    public int getCount() {
        int[] iArr = this.m_stringOffsets;
        if (iArr != null) {
            return iArr.length;
        }
        return 0;
    }

    public String getHTML(int i) {
        String string = getString(i);
        if (string == null) {
            return string;
        }
        int[] style = getStyle(i);
        if (style == null) {
            return ResXmlEncoders.escapeXmlChars(string);
        }
        StringBuilder sb = new StringBuilder(string.length() + 32);
        int[] iArr = new int[(style.length / 3)];
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int i4 = -1;
            for (int i5 = 0; i5 != style.length; i5 += 3) {
                int i6 = i5 + 1;
                if (style[i6] != -1 && (i4 == -1 || style[i4 + 1] > style[i6])) {
                    i4 = i5;
                }
            }
            int length = i4 != -1 ? style[i4 + 1] : string.length();
            int i7 = i2 - 1;
            while (i7 >= 0) {
                int i8 = iArr[i7];
                int i9 = style[i8 + 2];
                if (i9 >= length) {
                    break;
                }
                if (i3 <= i9) {
                    int i10 = i9 + 1;
                    sb.append(ResXmlEncoders.escapeXmlChars(string.substring(i3, i10)));
                    i3 = i10;
                }
                outputStyleTag(getString(style[i8]), sb, true);
                i7--;
            }
            int i11 = i7 + 1;
            if (i3 < length) {
                sb.append(ResXmlEncoders.escapeXmlChars(string.substring(i3, length)));
                i3 = length;
            }
            if (i4 == -1) {
                return sb.toString();
            }
            outputStyleTag(getString(style[i4]), sb, false);
            style[i4 + 1] = -1;
            iArr[i11] = i4;
            i2 = i11 + 1;
        }
    }

    public String getString(int i) {
        int[] iArr;
        int i2;
        int i3;
        if (i < 0 || (iArr = this.m_stringOffsets) == null || i >= iArr.length) {
            return null;
        }
        int i4 = iArr[i];
        if (!this.m_isUTF8) {
            i3 = getShort(this.m_strings, i4) * 2;
            i2 = i4 + 2;
        } else {
            int i5 = i4 + getVarint(this.m_strings, i4)[1];
            int[] varint = getVarint(this.m_strings, i5);
            i2 = i5 + varint[1];
            i3 = varint[0];
        }
        return decodeString(i2, i3);
    }
}
