package p005ch.qos.logback.core.util;

/* renamed from: ch.qos.logback.core.util.CharSequenceToRegexMapper */
class CharSequenceToRegexMapper {
    CharSequenceToRegexMapper() {
    }

    private String number(int i) {
        return "\\d{" + i + "}";
    }

    /* access modifiers changed from: package-private */
    public String toRegex(CharSequenceState charSequenceState) {
        StringBuilder sb;
        int i = charSequenceState.occurrences;
        char c = charSequenceState.f61c;
        char c2 = charSequenceState.f61c;
        if (c2 != 'y') {
            if (c2 == 'z') {
                return ".*";
            }
            switch (c2) {
                case '\'':
                    if (i == 1) {
                        return "";
                    }
                    throw new IllegalStateException("Too many single quotes");
                case '.':
                    return "\\.";
                case 'K':
                case 'S':
                case 'W':
                case 'd':
                case 'h':
                case 'k':
                case 'm':
                case 's':
                case 'w':
                    break;
                case 'M':
                    return i >= 3 ? ".{3,12}" : number(i);
                case 'Z':
                    return "(\\+|-)\\d{4}";
                case '\\':
                    throw new IllegalStateException("Forward slashes are not allowed");
                case 'a':
                    return ".{2}";
                default:
                    switch (c2) {
                        case 'D':
                        case 'F':
                        case 'H':
                            break;
                        case 'E':
                            return ".{2,12}";
                        case 'G':
                            return ".*";
                        default:
                            if (i == 1) {
                                sb.append("");
                                sb.append(c);
                            } else {
                                sb = new StringBuilder();
                                sb.append(c);
                                sb.append("{");
                                sb.append(i);
                                sb.append("}");
                            }
                            return sb.toString();
                    }
            }
        }
        return number(i);
    }
}
