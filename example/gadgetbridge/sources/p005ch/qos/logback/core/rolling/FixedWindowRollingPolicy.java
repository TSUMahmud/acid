package p005ch.qos.logback.core.rolling;

import java.io.File;
import java.util.Date;
import p005ch.qos.logback.core.CoreConstants;
import p005ch.qos.logback.core.rolling.helper.CompressionMode;
import p005ch.qos.logback.core.rolling.helper.Compressor;
import p005ch.qos.logback.core.rolling.helper.FileFilterUtil;
import p005ch.qos.logback.core.rolling.helper.FileNamePattern;
import p005ch.qos.logback.core.rolling.helper.RenameUtil;

/* renamed from: ch.qos.logback.core.rolling.FixedWindowRollingPolicy */
public class FixedWindowRollingPolicy extends RollingPolicyBase {
    static final String FNP_NOT_SET = "The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. ";
    private static int MAX_WINDOW_SIZE = 20;
    static final String PRUDENT_MODE_UNSUPPORTED = "See also http://logback.qos.ch/codes.html#tbr_fnp_prudent_unsupported";
    static final String SEE_PARENT_FN_NOT_SET = "Please refer to http://logback.qos.ch/codes.html#fwrp_parentFileName_not_set";
    public static final String ZIP_ENTRY_DATE_PATTERN = "yyyy-MM-dd_HHmm";
    Compressor compressor;
    int maxIndex = 7;
    int minIndex = 1;
    RenameUtil util = new RenameUtil();

    /* renamed from: ch.qos.logback.core.rolling.FixedWindowRollingPolicy$1 */
    static /* synthetic */ class C05231 {
        static final /* synthetic */ int[] $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode = new int[CompressionMode.values().length];

        static {
            try {
                $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[CompressionMode.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[CompressionMode.GZ.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[CompressionMode.ZIP.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private String transformFileNamePatternFromInt2Date(String str) {
        return FileFilterUtil.afterLastSlash(FileFilterUtil.slashify(str)).replace("%i", "%d{yyyy-MM-dd_HHmm}");
    }

    public String getActiveFileName() {
        return getParentsRawFileProperty();
    }

    public int getMaxIndex() {
        return this.maxIndex;
    }

    /* access modifiers changed from: protected */
    public int getMaxWindowSize() {
        return MAX_WINDOW_SIZE;
    }

    public int getMinIndex() {
        return this.minIndex;
    }

    public void rollover() throws RolloverFailure {
        String str;
        String str2;
        String str3;
        Compressor compressor2;
        if (this.maxIndex >= 0) {
            File file = new File(this.fileNamePattern.convertInt(this.maxIndex));
            if (file.exists()) {
                file.delete();
            }
            for (int i = this.maxIndex - 1; i >= this.minIndex; i--) {
                String convertInt = this.fileNamePattern.convertInt(i);
                if (new File(convertInt).exists()) {
                    this.util.rename(convertInt, this.fileNamePattern.convertInt(i + 1));
                } else {
                    addInfo("Skipping roll-over for inexistent file " + convertInt);
                }
            }
            int i2 = C05231.$SwitchMap$ch$qos$logback$core$rolling$helper$CompressionMode[this.compressionMode.ordinal()];
            if (i2 != 1) {
                if (i2 == 2) {
                    compressor2 = this.compressor;
                    str3 = getActiveFileName();
                    str2 = this.fileNamePattern.convertInt(this.minIndex);
                    str = null;
                } else if (i2 == 3) {
                    compressor2 = this.compressor;
                    str3 = getActiveFileName();
                    str2 = this.fileNamePattern.convertInt(this.minIndex);
                    str = this.zipEntryFileNamePattern.convert(new Date());
                } else {
                    return;
                }
                compressor2.compress(str3, str2, str);
                return;
            }
            this.util.rename(getActiveFileName(), this.fileNamePattern.convertInt(this.minIndex));
        }
    }

    public void setMaxIndex(int i) {
        this.maxIndex = i;
    }

    public void setMinIndex(int i) {
        this.minIndex = i;
    }

    public void start() {
        this.util.setContext(this.context);
        if (this.fileNamePatternStr != null) {
            this.fileNamePattern = new FileNamePattern(this.fileNamePatternStr, this.context);
            determineCompressionMode();
            if (isParentPrudent()) {
                addError("Prudent mode is not supported with FixedWindowRollingPolicy.");
                addError(PRUDENT_MODE_UNSUPPORTED);
                throw new IllegalStateException("Prudent mode is not supported.");
            } else if (getParentsRawFileProperty() != null) {
                if (this.maxIndex < this.minIndex) {
                    addWarn("MaxIndex (" + this.maxIndex + ") cannot be smaller than MinIndex (" + this.minIndex + ").");
                    addWarn("Setting maxIndex to equal minIndex.");
                    this.maxIndex = this.minIndex;
                }
                int maxWindowSize = getMaxWindowSize();
                if (this.maxIndex - this.minIndex > maxWindowSize) {
                    addWarn("Large window sizes are not allowed.");
                    this.maxIndex = this.minIndex + maxWindowSize;
                    addWarn("MaxIndex reduced to " + this.maxIndex);
                }
                if (this.fileNamePattern.getIntegerTokenConverter() != null) {
                    if (this.compressionMode == CompressionMode.ZIP) {
                        this.zipEntryFileNamePattern = new FileNamePattern(transformFileNamePatternFromInt2Date(this.fileNamePatternStr), this.context);
                    }
                    this.compressor = new Compressor(this.compressionMode);
                    this.compressor.setContext(this.context);
                    super.start();
                    return;
                }
                throw new IllegalStateException("FileNamePattern [" + this.fileNamePattern.getPattern() + "] does not contain a valid IntegerToken");
            } else {
                addError("The File name property must be set before using this rolling policy.");
                addError(SEE_PARENT_FN_NOT_SET);
                throw new IllegalStateException("The \"File\" option must be set.");
            }
        } else {
            addError(FNP_NOT_SET);
            addError(CoreConstants.SEE_FNP_NOT_SET);
            throw new IllegalStateException("The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
        }
    }
}
