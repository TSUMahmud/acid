package p005ch.qos.logback.core.util;

/* renamed from: ch.qos.logback.core.util.CharSequenceState */
class CharSequenceState {

    /* renamed from: c */
    final char f61c;
    int occurrences = 1;

    public CharSequenceState(char c) {
        this.f61c = c;
    }

    /* access modifiers changed from: package-private */
    public void incrementOccurrences() {
        this.occurrences++;
    }
}
