package nodomain.freeyourgadget.gadgetbridge.service.devices.pebble.webview;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import nodomain.freeyourgadget.gadgetbridge.util.C1238GB;

public class GBChromeClient extends WebChromeClient {
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (ConsoleMessage.MessageLevel.ERROR.equals(consoleMessage.messageLevel())) {
            C1238GB.toast(formatConsoleMessage(consoleMessage), 1, 3);
        }
        return super.onConsoleMessage(consoleMessage);
    }

    private static String formatConsoleMessage(ConsoleMessage message) {
        String sourceId = message.sourceId();
        if (sourceId == null || sourceId.length() == 0) {
            sourceId = "unknown";
        }
        return String.format("%s (at %s: %d)", new Object[]{message.message(), sourceId, Integer.valueOf(message.lineNumber())});
    }
}
