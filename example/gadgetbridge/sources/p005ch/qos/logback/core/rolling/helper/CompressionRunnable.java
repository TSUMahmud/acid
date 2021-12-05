package p005ch.qos.logback.core.rolling.helper;

/* renamed from: ch.qos.logback.core.rolling.helper.CompressionRunnable */
public class CompressionRunnable implements Runnable {
    final Compressor compressor;
    final String innerEntryName;
    final String nameOfCompressedFile;
    final String nameOfFile2Compress;

    public CompressionRunnable(Compressor compressor2, String str, String str2, String str3) {
        this.compressor = compressor2;
        this.nameOfFile2Compress = str;
        this.nameOfCompressedFile = str2;
        this.innerEntryName = str3;
    }

    public void run() {
        this.compressor.compress(this.nameOfFile2Compress, this.nameOfCompressedFile, this.innerEntryName);
    }
}
