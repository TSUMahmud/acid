package p005ch.qos.logback.core.property;

import java.io.File;
import p005ch.qos.logback.core.PropertyDefinerBase;
import p005ch.qos.logback.core.util.OptionHelper;

/* renamed from: ch.qos.logback.core.property.FileExistsPropertyDefiner */
public class FileExistsPropertyDefiner extends PropertyDefinerBase {
    String path;

    public String getPath() {
        return this.path;
    }

    public String getPropertyValue() {
        if (!OptionHelper.isEmpty(this.path)) {
            return booleanAsStr(new File(this.path).exists());
        }
        addError("The \"path\" property must be set.");
        return null;
    }

    public void setPath(String str) {
        this.path = str;
    }
}
