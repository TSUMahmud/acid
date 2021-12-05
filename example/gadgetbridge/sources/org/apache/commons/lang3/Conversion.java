package org.apache.commons.lang3;

import java.util.UUID;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public class Conversion {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final boolean[] FFFF = {false, false, false, false};
    private static final boolean[] FFFT = {false, false, false, true};
    private static final boolean[] FFTF = {false, false, true, false};
    private static final boolean[] FFTT = {false, false, true, true};
    private static final boolean[] FTFF = {false, true, false, false};
    private static final boolean[] FTFT = {false, true, false, true};
    private static final boolean[] FTTF = {false, true, true, false};
    private static final boolean[] FTTT = {false, true, true, true};
    private static final boolean[] TFFF = {true, false, false, false};
    private static final boolean[] TFFT = {true, false, false, true};
    private static final boolean[] TFTF = {true, false, true, false};
    private static final boolean[] TFTT = {true, false, true, true};
    private static final boolean[] TTFF = {true, true, false, false};
    private static final boolean[] TTFT = {true, true, false, true};
    private static final boolean[] TTTF = {true, true, true, false};
    private static final boolean[] TTTT = {true, true, true, true};

    public static int hexDigitToInt(char hexDigit) {
        int digit = Character.digit(hexDigit, 16);
        if (digit >= 0) {
            return digit;
        }
        throw new IllegalArgumentException("Cannot interpret '" + hexDigit + "' as a hexadecimal digit");
    }

    public static int hexDigitMsb0ToInt(char hexDigit) {
        switch (hexDigit) {
            case '0':
                return 0;
            case '1':
                return 8;
            case '2':
                return 4;
            case '3':
                return 12;
            case '4':
                return 2;
            case '5':
                return 10;
            case '6':
                return 6;
            case '7':
                return 14;
            case '8':
                return 1;
            case '9':
                return 9;
            default:
                switch (hexDigit) {
                    case 'A':
                        return 5;
                    case 'B':
                        return 13;
                    case 'C':
                        return 3;
                    case 'D':
                        return 11;
                    case 'E':
                        return 7;
                    case 'F':
                        return 15;
                    default:
                        switch (hexDigit) {
                            case 'a':
                                return 5;
                            case 'b':
                                return 13;
                            case 'c':
                                return 3;
                            case 'd':
                                return 11;
                            case 'e':
                                return 7;
                            case 'f':
                                return 15;
                            default:
                                throw new IllegalArgumentException("Cannot interpret '" + hexDigit + "' as a hexadecimal digit");
                        }
                }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003f, code lost:
        return (boolean[]) TFTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0048, code lost:
        return (boolean[]) FFTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0051, code lost:
        return (boolean[]) TTFT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005a, code lost:
        return (boolean[]) FTFT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x002d, code lost:
        return (boolean[]) TTTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0036, code lost:
        return (boolean[]) FTTT.clone();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean[] hexDigitToBinary(char r3) {
        /*
            switch(r3) {
                case 48: goto L_0x00ac;
                case 49: goto L_0x00a3;
                case 50: goto L_0x009a;
                case 51: goto L_0x0091;
                case 52: goto L_0x0088;
                case 53: goto L_0x007f;
                case 54: goto L_0x0076;
                case 55: goto L_0x006d;
                case 56: goto L_0x0064;
                case 57: goto L_0x005b;
                default: goto L_0x0003;
            }
        L_0x0003:
            switch(r3) {
                case 65: goto L_0x0052;
                case 66: goto L_0x0049;
                case 67: goto L_0x0040;
                case 68: goto L_0x0037;
                case 69: goto L_0x002e;
                case 70: goto L_0x0025;
                default: goto L_0x0006;
            }
        L_0x0006:
            switch(r3) {
                case 97: goto L_0x0052;
                case 98: goto L_0x0049;
                case 99: goto L_0x0040;
                case 100: goto L_0x0037;
                case 101: goto L_0x002e;
                case 102: goto L_0x0025;
                default: goto L_0x0009;
            }
        L_0x0009:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot interpret '"
            r1.append(r2)
            r1.append(r3)
            java.lang.String r2 = "' as a hexadecimal digit"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0025:
            boolean[] r0 = TTTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x002e:
            boolean[] r0 = FTTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0037:
            boolean[] r0 = TFTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0040:
            boolean[] r0 = FFTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0049:
            boolean[] r0 = TTFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0052:
            boolean[] r0 = FTFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x005b:
            boolean[] r0 = TFFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0064:
            boolean[] r0 = FFFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x006d:
            boolean[] r0 = TTTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0076:
            boolean[] r0 = FTTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x007f:
            boolean[] r0 = TFTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0088:
            boolean[] r0 = FFTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0091:
            boolean[] r0 = TTFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x009a:
            boolean[] r0 = FTFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x00a3:
            boolean[] r0 = TFFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x00ac:
            boolean[] r0 = FFFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitToBinary(char):boolean[]");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003f, code lost:
        return (boolean[]) TTFT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0048, code lost:
        return (boolean[]) TTFF.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0051, code lost:
        return (boolean[]) TFTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x005a, code lost:
        return (boolean[]) TFTF.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x002d, code lost:
        return (boolean[]) TTTT.clone();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0036, code lost:
        return (boolean[]) TTTF.clone();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean[] hexDigitMsb0ToBinary(char r3) {
        /*
            switch(r3) {
                case 48: goto L_0x00ac;
                case 49: goto L_0x00a3;
                case 50: goto L_0x009a;
                case 51: goto L_0x0091;
                case 52: goto L_0x0088;
                case 53: goto L_0x007f;
                case 54: goto L_0x0076;
                case 55: goto L_0x006d;
                case 56: goto L_0x0064;
                case 57: goto L_0x005b;
                default: goto L_0x0003;
            }
        L_0x0003:
            switch(r3) {
                case 65: goto L_0x0052;
                case 66: goto L_0x0049;
                case 67: goto L_0x0040;
                case 68: goto L_0x0037;
                case 69: goto L_0x002e;
                case 70: goto L_0x0025;
                default: goto L_0x0006;
            }
        L_0x0006:
            switch(r3) {
                case 97: goto L_0x0052;
                case 98: goto L_0x0049;
                case 99: goto L_0x0040;
                case 100: goto L_0x0037;
                case 101: goto L_0x002e;
                case 102: goto L_0x0025;
                default: goto L_0x0009;
            }
        L_0x0009:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot interpret '"
            r1.append(r2)
            r1.append(r3)
            java.lang.String r2 = "' as a hexadecimal digit"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x0025:
            boolean[] r0 = TTTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x002e:
            boolean[] r0 = TTTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0037:
            boolean[] r0 = TTFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0040:
            boolean[] r0 = TTFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0049:
            boolean[] r0 = TFTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0052:
            boolean[] r0 = TFTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x005b:
            boolean[] r0 = TFFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0064:
            boolean[] r0 = TFFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x006d:
            boolean[] r0 = FTTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0076:
            boolean[] r0 = FTTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x007f:
            boolean[] r0 = FTFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0088:
            boolean[] r0 = FTFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x0091:
            boolean[] r0 = FFTT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x009a:
            boolean[] r0 = FFTF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x00a3:
            boolean[] r0 = FFFT
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        L_0x00ac:
            boolean[] r0 = FFFF
            java.lang.Object r0 = r0.clone()
            boolean[] r0 = (boolean[]) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.lang3.Conversion.hexDigitMsb0ToBinary(char):boolean[]");
    }

    public static char binaryToHexDigit(boolean[] src) {
        return binaryToHexDigit(src, 0);
    }

    public static char binaryToHexDigit(boolean[] src, int srcPos) {
        if (src.length != 0) {
            return (src.length <= srcPos + 3 || !src[srcPos + 3]) ? (src.length <= srcPos + 2 || !src[srcPos + 2]) ? (src.length <= srcPos + 1 || !src[srcPos + 1]) ? src[srcPos] ? '1' : '0' : src[srcPos] ? '3' : '2' : (src.length <= srcPos + 1 || !src[srcPos + 1]) ? src[srcPos] ? '5' : '4' : src[srcPos] ? '7' : '6' : (src.length <= srcPos + 2 || !src[srcPos + 2]) ? (src.length <= srcPos + 1 || !src[srcPos + 1]) ? src[srcPos] ? '9' : '8' : src[srcPos] ? 'b' : 'a' : (src.length <= srcPos + 1 || !src[srcPos + 1]) ? src[srcPos] ? 'd' : 'c' : src[srcPos] ? 'f' : 'e';
        }
        throw new IllegalArgumentException("Cannot convert an empty array.");
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] src) {
        return binaryToHexDigitMsb0_4bits(src, 0);
    }

    public static char binaryToHexDigitMsb0_4bits(boolean[] src, int srcPos) {
        if (src.length > 8) {
            throw new IllegalArgumentException("src.length>8: src.length=" + src.length);
        } else if (src.length - srcPos >= 4) {
            return src[srcPos + 3] ? src[srcPos + 2] ? src[srcPos + 1] ? src[srcPos] ? 'f' : '7' : src[srcPos] ? 'b' : '3' : src[srcPos + 1] ? src[srcPos] ? 'd' : '5' : src[srcPos] ? '9' : '1' : src[srcPos + 2] ? src[srcPos + 1] ? src[srcPos] ? 'e' : '6' : src[srcPos] ? 'a' : '2' : src[srcPos + 1] ? src[srcPos] ? 'c' : '4' : src[srcPos] ? '8' : '0';
        } else {
            throw new IllegalArgumentException("src.length-srcPos<4: src.length=" + src.length + ", srcPos=" + srcPos);
        }
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] src) {
        return binaryBeMsb0ToHexDigit(src, 0);
    }

    public static char binaryBeMsb0ToHexDigit(boolean[] src, int srcPos) {
        if (src.length != 0) {
            int beSrcPos = (src.length - 1) - srcPos;
            int srcLen = Math.min(4, beSrcPos + 1);
            boolean[] paddedSrc = new boolean[4];
            System.arraycopy(src, (beSrcPos + 1) - srcLen, paddedSrc, 4 - srcLen, srcLen);
            boolean[] src2 = paddedSrc;
            return src2[0] ? (src2.length <= 0 + 1 || !src2[0 + 1]) ? (src2.length <= 0 + 2 || !src2[0 + 2]) ? (src2.length <= 0 + 3 || !src2[0 + 3]) ? '8' : '9' : (src2.length <= 0 + 3 || !src2[0 + 3]) ? 'a' : 'b' : (src2.length <= 0 + 2 || !src2[0 + 2]) ? (src2.length <= 0 + 3 || !src2[0 + 3]) ? 'c' : 'd' : (src2.length <= 0 + 3 || !src2[0 + 3]) ? 'e' : 'f' : (src2.length <= 0 + 1 || !src2[0 + 1]) ? (src2.length <= 0 + 2 || !src2[0 + 2]) ? (src2.length <= 0 + 3 || !src2[0 + 3]) ? '0' : '1' : (src2.length <= 0 + 3 || !src2[0 + 3]) ? '2' : '3' : (src2.length <= 0 + 2 || !src2[0 + 2]) ? (src2.length <= 0 + 3 || !src2[0 + 3]) ? '4' : '5' : (src2.length <= 0 + 3 || !src2[0 + 3]) ? '6' : '7';
        }
        throw new IllegalArgumentException("Cannot convert an empty array.");
    }

    public static char intToHexDigit(int nibble) {
        char c = Character.forDigit(nibble, 16);
        if (c != 0) {
            return c;
        }
        throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
    }

    public static char intToHexDigitMsb0(int nibble) {
        switch (nibble) {
            case 0:
                return '0';
            case 1:
                return '8';
            case 2:
                return '4';
            case 3:
                return 'c';
            case 4:
                return '2';
            case 5:
                return 'a';
            case 6:
                return '6';
            case 7:
                return 'e';
            case 8:
                return '1';
            case 9:
                return '9';
            case 10:
                return '5';
            case 11:
                return 'd';
            case 12:
                return '3';
            case 13:
                return 'b';
            case 14:
                return '7';
            case 15:
                return 'f';
            default:
                throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
        }
    }

    public static long intArrayToLong(int[] src, int srcPos, long dstInit, int dstPos, int nInts) {
        if ((src.length == 0 && srcPos == 0) || nInts == 0) {
            return dstInit;
        }
        if (((nInts - 1) * 32) + dstPos < 64) {
            long out = dstInit;
            for (int i = 0; i < nInts; i++) {
                int shift = (i * 32) + dstPos;
                out = ((-1 ^ (4294967295 << shift)) & out) | ((((long) src[i + srcPos]) & 4294967295L) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("(nInts-1)*32+dstPos is greater or equal to than 64");
    }

    public static long shortArrayToLong(short[] src, int srcPos, long dstInit, int dstPos, int nShorts) {
        if ((src.length == 0 && srcPos == 0) || nShorts == 0) {
            return dstInit;
        }
        if (((nShorts - 1) * 16) + dstPos < 64) {
            long out = dstInit;
            for (int i = 0; i < nShorts; i++) {
                int shift = (i * 16) + dstPos;
                out = ((-1 ^ (65535 << shift)) & out) | ((((long) src[i + srcPos]) & 65535) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greater or equal to than 64");
    }

    public static int shortArrayToInt(short[] src, int srcPos, int dstInit, int dstPos, int nShorts) {
        if ((src.length == 0 && srcPos == 0) || nShorts == 0) {
            return dstInit;
        }
        if (((nShorts - 1) * 16) + dstPos < 32) {
            int out = dstInit;
            for (int i = 0; i < nShorts; i++) {
                int shift = (i * 16) + dstPos;
                out = (((65535 << shift) ^ -1) & out) | ((src[i + srcPos] & GBDevice.BATTERY_UNKNOWN) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greater or equal to than 32");
    }

    public static long byteArrayToLong(byte[] src, int srcPos, long dstInit, int dstPos, int nBytes) {
        if ((src.length == 0 && srcPos == 0) || nBytes == 0) {
            return dstInit;
        }
        if (((nBytes - 1) * 8) + dstPos < 64) {
            long out = dstInit;
            for (int i = 0; i < nBytes; i++) {
                int shift = (i * 8) + dstPos;
                out = ((-1 ^ (255 << shift)) & out) | ((((long) src[i + srcPos]) & 255) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greater or equal to than 64");
    }

    public static int byteArrayToInt(byte[] src, int srcPos, int dstInit, int dstPos, int nBytes) {
        if ((src.length == 0 && srcPos == 0) || nBytes == 0) {
            return dstInit;
        }
        if (((nBytes - 1) * 8) + dstPos < 32) {
            int out = dstInit;
            for (int i = 0; i < nBytes; i++) {
                int shift = (i * 8) + dstPos;
                out = (((255 << shift) ^ -1) & out) | ((src[i + srcPos] & 255) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greater or equal to than 32");
    }

    public static short byteArrayToShort(byte[] src, int srcPos, short dstInit, int dstPos, int nBytes) {
        if ((src.length == 0 && srcPos == 0) || nBytes == 0) {
            return dstInit;
        }
        if (((nBytes - 1) * 8) + dstPos < 16) {
            short out = dstInit;
            for (int i = 0; i < nBytes; i++) {
                int shift = (i * 8) + dstPos;
                out = (short) ((((255 << shift) ^ -1) & out) | ((src[i + srcPos] & 255) << shift));
            }
            return out;
        }
        throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greater or equal to than 16");
    }

    public static long hexToLong(String src, int srcPos, long dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos < 64) {
            long out = dstInit;
            for (int i = 0; i < nHex; i++) {
                int shift = (i * 4) + dstPos;
                out = ((-1 ^ (15 << shift)) & out) | ((((long) hexDigitToInt(src.charAt(i + srcPos))) & 15) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 64");
    }

    public static int hexToInt(String src, int srcPos, int dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos < 32) {
            int out = dstInit;
            for (int i = 0; i < nHex; i++) {
                int shift = (i * 4) + dstPos;
                out = (((15 << shift) ^ -1) & out) | ((hexDigitToInt(src.charAt(i + srcPos)) & 15) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 32");
    }

    public static short hexToShort(String src, int srcPos, short dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos < 16) {
            short out = dstInit;
            for (int i = 0; i < nHex; i++) {
                int shift = (i * 4) + dstPos;
                out = (short) ((((15 << shift) ^ -1) & out) | ((hexDigitToInt(src.charAt(i + srcPos)) & 15) << shift));
            }
            return out;
        }
        throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 16");
    }

    public static byte hexToByte(String src, int srcPos, byte dstInit, int dstPos, int nHex) {
        if (nHex == 0) {
            return dstInit;
        }
        if (((nHex - 1) * 4) + dstPos < 8) {
            byte out = dstInit;
            for (int i = 0; i < nHex; i++) {
                int shift = (i * 4) + dstPos;
                out = (byte) ((((15 << shift) ^ -1) & out) | ((hexDigitToInt(src.charAt(i + srcPos)) & 15) << shift));
            }
            return out;
        }
        throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greater or equal to than 8");
    }

    public static long binaryToLong(boolean[] src, int srcPos, long dstInit, int dstPos, int nBools) {
        boolean[] zArr = src;
        int i = nBools;
        if ((zArr.length == 0 && srcPos == 0) || i == 0) {
            return dstInit;
        }
        if ((i - 1) + dstPos < 64) {
            long out = dstInit;
            for (int i2 = 0; i2 < i; i2++) {
                int shift = i2 + dstPos;
                out = ((-1 ^ (1 << shift)) & out) | ((zArr[i2 + srcPos] ? 1 : 0) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 64");
    }

    public static int binaryToInt(boolean[] src, int srcPos, int dstInit, int dstPos, int nBools) {
        if ((src.length == 0 && srcPos == 0) || nBools == 0) {
            return dstInit;
        }
        if ((nBools - 1) + dstPos < 32) {
            int out = dstInit;
            for (int i = 0; i < nBools; i++) {
                int shift = i + dstPos;
                out = (((1 << shift) ^ -1) & out) | ((src[i + srcPos] ? 1 : 0) << shift);
            }
            return out;
        }
        throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 32");
    }

    public static short binaryToShort(boolean[] src, int srcPos, short dstInit, int dstPos, int nBools) {
        if ((src.length == 0 && srcPos == 0) || nBools == 0) {
            return dstInit;
        }
        if ((nBools - 1) + dstPos < 16) {
            short out = dstInit;
            for (int i = 0; i < nBools; i++) {
                int shift = i + dstPos;
                out = (short) ((((1 << shift) ^ -1) & out) | ((src[i + srcPos] ? 1 : 0) << shift));
            }
            return out;
        }
        throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 16");
    }

    public static byte binaryToByte(boolean[] src, int srcPos, byte dstInit, int dstPos, int nBools) {
        if ((src.length == 0 && srcPos == 0) || nBools == 0) {
            return dstInit;
        }
        if ((nBools - 1) + dstPos < 8) {
            byte out = dstInit;
            for (int i = 0; i < nBools; i++) {
                int shift = i + dstPos;
                out = (byte) ((((1 << shift) ^ -1) & out) | ((src[i + srcPos] ? 1 : 0) << shift));
            }
            return out;
        }
        throw new IllegalArgumentException("nBools-1+dstPos is greater or equal to than 8");
    }

    public static int[] longToIntArray(long src, int srcPos, int[] dst, int dstPos, int nInts) {
        if (nInts == 0) {
            return dst;
        }
        if (((nInts - 1) * 32) + srcPos < 64) {
            for (int i = 0; i < nInts; i++) {
                dst[dstPos + i] = (int) (-1 & (src >> ((i * 32) + srcPos)));
            }
            return dst;
        }
        throw new IllegalArgumentException("(nInts-1)*32+srcPos is greater or equal to than 64");
    }

    public static short[] longToShortArray(long src, int srcPos, short[] dst, int dstPos, int nShorts) {
        if (nShorts == 0) {
            return dst;
        }
        if (((nShorts - 1) * 16) + srcPos < 64) {
            for (int i = 0; i < nShorts; i++) {
                dst[dstPos + i] = (short) ((int) (65535 & (src >> ((i * 16) + srcPos))));
            }
            return dst;
        }
        throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greater or equal to than 64");
    }

    public static short[] intToShortArray(int src, int srcPos, short[] dst, int dstPos, int nShorts) {
        if (nShorts == 0) {
            return dst;
        }
        if (((nShorts - 1) * 16) + srcPos < 32) {
            for (int i = 0; i < nShorts; i++) {
                dst[dstPos + i] = (short) (65535 & (src >> ((i * 16) + srcPos)));
            }
            return dst;
        }
        throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greater or equal to than 32");
    }

    public static byte[] longToByteArray(long src, int srcPos, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (((nBytes - 1) * 8) + srcPos < 64) {
            for (int i = 0; i < nBytes; i++) {
                dst[dstPos + i] = (byte) ((int) (255 & (src >> ((i * 8) + srcPos))));
            }
            return dst;
        }
        throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 64");
    }

    public static byte[] intToByteArray(int src, int srcPos, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (((nBytes - 1) * 8) + srcPos < 32) {
            for (int i = 0; i < nBytes; i++) {
                dst[dstPos + i] = (byte) ((src >> ((i * 8) + srcPos)) & 255);
            }
            return dst;
        }
        throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 32");
    }

    public static byte[] shortToByteArray(short src, int srcPos, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (((nBytes - 1) * 8) + srcPos < 16) {
            for (int i = 0; i < nBytes; i++) {
                dst[dstPos + i] = (byte) ((src >> ((i * 8) + srcPos)) & 255);
            }
            return dst;
        }
        throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greater or equal to than 16");
    }

    public static String longToHex(long src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos < 64) {
            StringBuilder sb = new StringBuilder(dstInit);
            int append = sb.length();
            for (int i = 0; i < nHexs; i++) {
                int bits = (int) (15 & (src >> ((i * 4) + srcPos)));
                if (dstPos + i == append) {
                    append++;
                    sb.append(intToHexDigit(bits));
                } else {
                    sb.setCharAt(dstPos + i, intToHexDigit(bits));
                }
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 64");
    }

    public static String intToHex(int src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos < 32) {
            StringBuilder sb = new StringBuilder(dstInit);
            int append = sb.length();
            for (int i = 0; i < nHexs; i++) {
                int bits = (src >> ((i * 4) + srcPos)) & 15;
                if (dstPos + i == append) {
                    append++;
                    sb.append(intToHexDigit(bits));
                } else {
                    sb.setCharAt(dstPos + i, intToHexDigit(bits));
                }
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 32");
    }

    public static String shortToHex(short src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos < 16) {
            StringBuilder sb = new StringBuilder(dstInit);
            int append = sb.length();
            for (int i = 0; i < nHexs; i++) {
                int bits = (src >> ((i * 4) + srcPos)) & 15;
                if (dstPos + i == append) {
                    append++;
                    sb.append(intToHexDigit(bits));
                } else {
                    sb.setCharAt(dstPos + i, intToHexDigit(bits));
                }
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 16");
    }

    public static String byteToHex(byte src, int srcPos, String dstInit, int dstPos, int nHexs) {
        if (nHexs == 0) {
            return dstInit;
        }
        if (((nHexs - 1) * 4) + srcPos < 8) {
            StringBuilder sb = new StringBuilder(dstInit);
            int append = sb.length();
            for (int i = 0; i < nHexs; i++) {
                int bits = (src >> ((i * 4) + srcPos)) & 15;
                if (dstPos + i == append) {
                    append++;
                    sb.append(intToHexDigit(bits));
                } else {
                    sb.setCharAt(dstPos + i, intToHexDigit(bits));
                }
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greater or equal to than 8");
    }

    public static boolean[] longToBinary(long src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos < 64) {
            for (int i = 0; i < nBools; i++) {
                dst[dstPos + i] = (1 & (src >> (i + srcPos))) != 0;
            }
            return dst;
        }
        throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 64");
    }

    public static boolean[] intToBinary(int src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos < 32) {
            for (int i = 0; i < nBools; i++) {
                int i2 = dstPos + i;
                boolean z = true;
                if (((src >> (i + srcPos)) & 1) == 0) {
                    z = false;
                }
                dst[i2] = z;
            }
            return dst;
        }
        throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 32");
    }

    public static boolean[] shortToBinary(short src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos < 16) {
            for (int i = 0; i < nBools; i++) {
                int i2 = dstPos + i;
                boolean z = true;
                if (((src >> (i + srcPos)) & 1) == 0) {
                    z = false;
                }
                dst[i2] = z;
            }
            return dst;
        }
        throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 16");
    }

    public static boolean[] byteToBinary(byte src, int srcPos, boolean[] dst, int dstPos, int nBools) {
        if (nBools == 0) {
            return dst;
        }
        if ((nBools - 1) + srcPos < 8) {
            for (int i = 0; i < nBools; i++) {
                int i2 = dstPos + i;
                boolean z = true;
                if (((src >> (i + srcPos)) & 1) == 0) {
                    z = false;
                }
                dst[i2] = z;
            }
            return dst;
        }
        throw new IllegalArgumentException("nBools-1+srcPos is greater or equal to than 8");
    }

    public static byte[] uuidToByteArray(UUID src, byte[] dst, int dstPos, int nBytes) {
        if (nBytes == 0) {
            return dst;
        }
        if (nBytes <= 16) {
            longToByteArray(src.getMostSignificantBits(), 0, dst, dstPos, nBytes > 8 ? 8 : nBytes);
            if (nBytes >= 8) {
                longToByteArray(src.getLeastSignificantBits(), 0, dst, dstPos + 8, nBytes - 8);
            }
            return dst;
        }
        throw new IllegalArgumentException("nBytes is greater than 16");
    }

    public static UUID byteArrayToUuid(byte[] src, int srcPos) {
        if (src.length - srcPos >= 16) {
            return new UUID(byteArrayToLong(src, srcPos, 0, 0, 8), byteArrayToLong(src, srcPos + 8, 0, 0, 8));
        }
        throw new IllegalArgumentException("Need at least 16 bytes for UUID");
    }
}
