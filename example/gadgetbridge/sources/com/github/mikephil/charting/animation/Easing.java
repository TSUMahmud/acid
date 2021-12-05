package com.github.mikephil.charting.animation;

import android.animation.TimeInterpolator;

public class Easing {
    private static final float DOUBLE_PI = 6.2831855f;
    public static final EasingFunction EaseInBack = new EasingFunction() {
        public float getInterpolation(float input) {
            return input * input * ((2.70158f * input) - 1.70158f);
        }
    };
    public static final EasingFunction EaseInBounce = new EasingFunction() {
        public float getInterpolation(float input) {
            return 1.0f - Easing.EaseOutBounce.getInterpolation(1.0f - input);
        }
    };
    public static final EasingFunction EaseInCirc = new EasingFunction() {
        public float getInterpolation(float input) {
            return -(((float) Math.sqrt((double) (1.0f - (input * input)))) - 1.0f);
        }
    };
    public static final EasingFunction EaseInCubic = new EasingFunction() {
        public float getInterpolation(float input) {
            return (float) Math.pow((double) input, 3.0d);
        }
    };
    public static final EasingFunction EaseInElastic = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input == 0.0f) {
                return 0.0f;
            }
            if (input == 1.0f) {
                return 1.0f;
            }
            float input2 = input - 1.0f;
            return -(((float) Math.pow(2.0d, (double) (input2 * 10.0f))) * ((float) Math.sin((double) (((input2 - ((0.3f / Easing.DOUBLE_PI) * ((float) Math.asin(1.0d)))) * Easing.DOUBLE_PI) / 0.3f))));
        }
    };
    public static final EasingFunction EaseInExpo = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input == 0.0f) {
                return 0.0f;
            }
            return (float) Math.pow(2.0d, (double) ((input - 1.0f) * 10.0f));
        }
    };
    public static final EasingFunction EaseInOutBack = new EasingFunction() {
        public float getInterpolation(float input) {
            float input2 = input * 2.0f;
            if (input2 < 1.0f) {
                float s = 1.525f * 1.70158f;
                return input2 * input2 * (((s + 1.0f) * input2) - s) * 0.5f;
            }
            float f = input2 - 2.0f;
            float input3 = f;
            float s2 = 1.525f * 1.70158f;
            return ((f * input3 * (((s2 + 1.0f) * input3) + s2)) + 2.0f) * 0.5f;
        }
    };
    public static final EasingFunction EaseInOutBounce = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input < 0.5f) {
                return Easing.EaseInBounce.getInterpolation(2.0f * input) * 0.5f;
            }
            return (Easing.EaseOutBounce.getInterpolation((2.0f * input) - 1.0f) * 0.5f) + 0.5f;
        }
    };
    public static final EasingFunction EaseInOutCirc = new EasingFunction() {
        public float getInterpolation(float input) {
            float input2 = input * 2.0f;
            if (input2 < 1.0f) {
                return (((float) Math.sqrt((double) (1.0f - (input2 * input2)))) - 1.0f) * -0.5f;
            }
            float input3 = input2 - 2.0f;
            return (((float) Math.sqrt((double) (1.0f - (input3 * input3)))) + 1.0f) * 0.5f;
        }
    };
    public static final EasingFunction EaseInOutCubic = new EasingFunction() {
        public float getInterpolation(float input) {
            float input2 = input * 2.0f;
            if (input2 < 1.0f) {
                return ((float) Math.pow((double) input2, 3.0d)) * 0.5f;
            }
            return (((float) Math.pow((double) (input2 - 2.0f), 3.0d)) + 2.0f) * 0.5f;
        }
    };
    public static final EasingFunction EaseInOutElastic = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input == 0.0f) {
                return 0.0f;
            }
            float input2 = input * 2.0f;
            if (input2 == 2.0f) {
                return 1.0f;
            }
            float s = ((float) Math.asin(1.0d)) * 0.07161972f;
            if (input2 < 1.0f) {
                float input3 = input2 - 1.0f;
                return ((float) Math.pow(2.0d, (double) (input3 * 10.0f))) * ((float) Math.sin((double) (((1.0f * input3) - s) * Easing.DOUBLE_PI * 2.2222223f))) * -0.5f;
            }
            float input4 = input2 - 1.0f;
            return (((float) Math.pow(2.0d, (double) (input4 * -10.0f))) * 0.5f * ((float) Math.sin((double) (((input4 * 1.0f) - s) * Easing.DOUBLE_PI * 2.2222223f)))) + 1.0f;
        }
    };
    public static final EasingFunction EaseInOutExpo = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input == 0.0f) {
                return 0.0f;
            }
            if (input == 1.0f) {
                return 1.0f;
            }
            float input2 = input * 2.0f;
            if (input2 < 1.0f) {
                return ((float) Math.pow(2.0d, (double) ((input2 - 1.0f) * 10.0f))) * 0.5f;
            }
            float f = input2 - 1.0f;
            float input3 = f;
            return ((-((float) Math.pow(2.0d, (double) (f * -10.0f)))) + 2.0f) * 0.5f;
        }
    };
    public static final EasingFunction EaseInOutQuad = new EasingFunction() {
        public float getInterpolation(float input) {
            float input2 = input * 2.0f;
            if (input2 < 1.0f) {
                return 0.5f * input2 * input2;
            }
            float input3 = input2 - 1.0f;
            return ((input3 * (input3 - 2.0f)) - 1.0f) * -0.5f;
        }
    };
    public static final EasingFunction EaseInOutQuart = new EasingFunction() {
        public float getInterpolation(float input) {
            float input2 = input * 2.0f;
            if (input2 < 1.0f) {
                return ((float) Math.pow((double) input2, 4.0d)) * 0.5f;
            }
            return (((float) Math.pow((double) (input2 - 2.0f), 4.0d)) - 2.0f) * -0.5f;
        }
    };
    public static final EasingFunction EaseInOutSine = new EasingFunction() {
        public float getInterpolation(float input) {
            double d = (double) input;
            Double.isNaN(d);
            return (((float) Math.cos(d * 3.141592653589793d)) - 1.0f) * -0.5f;
        }
    };
    public static final EasingFunction EaseInQuad = new EasingFunction() {
        public float getInterpolation(float input) {
            return input * input;
        }
    };
    public static final EasingFunction EaseInQuart = new EasingFunction() {
        public float getInterpolation(float input) {
            return (float) Math.pow((double) input, 4.0d);
        }
    };
    public static final EasingFunction EaseInSine = new EasingFunction() {
        public float getInterpolation(float input) {
            double d = (double) input;
            Double.isNaN(d);
            return (-((float) Math.cos(d * 1.5707963267948966d))) + 1.0f;
        }
    };
    public static final EasingFunction EaseOutBack = new EasingFunction() {
        public float getInterpolation(float input) {
            float input2 = input - 1.0f;
            return (input2 * input2 * ((2.70158f * input2) + 1.70158f)) + 1.0f;
        }
    };
    public static final EasingFunction EaseOutBounce = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input < 0.36363637f) {
                return 7.5625f * input * input;
            }
            if (input < 0.72727275f) {
                float input2 = input - 0.54545456f;
                return (input2 * 7.5625f * input2) + 0.75f;
            } else if (input < 0.90909094f) {
                float input3 = input - 0.8181818f;
                return (input3 * 7.5625f * input3) + 0.9375f;
            } else {
                float input4 = input - 0.95454544f;
                return (input4 * 7.5625f * input4) + 0.984375f;
            }
        }
    };
    public static final EasingFunction EaseOutCirc = new EasingFunction() {
        public float getInterpolation(float input) {
            float input2 = input - 1.0f;
            return (float) Math.sqrt((double) (1.0f - (input2 * input2)));
        }
    };
    public static final EasingFunction EaseOutCubic = new EasingFunction() {
        public float getInterpolation(float input) {
            return ((float) Math.pow((double) (input - 1.0f), 3.0d)) + 1.0f;
        }
    };
    public static final EasingFunction EaseOutElastic = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input == 0.0f) {
                return 0.0f;
            }
            if (input == 1.0f) {
                return 1.0f;
            }
            return (((float) Math.pow(2.0d, (double) (-10.0f * input))) * ((float) Math.sin((double) (((input - ((0.3f / Easing.DOUBLE_PI) * ((float) Math.asin(1.0d)))) * Easing.DOUBLE_PI) / 0.3f)))) + 1.0f;
        }
    };
    public static final EasingFunction EaseOutExpo = new EasingFunction() {
        public float getInterpolation(float input) {
            if (input == 1.0f) {
                return 1.0f;
            }
            return -((float) Math.pow(2.0d, (double) ((1.0f + input) * -10.0f)));
        }
    };
    public static final EasingFunction EaseOutQuad = new EasingFunction() {
        public float getInterpolation(float input) {
            return (-input) * (input - 2.0f);
        }
    };
    public static final EasingFunction EaseOutQuart = new EasingFunction() {
        public float getInterpolation(float input) {
            return -(((float) Math.pow((double) (input - 1.0f), 4.0d)) - 1.0f);
        }
    };
    public static final EasingFunction EaseOutSine = new EasingFunction() {
        public float getInterpolation(float input) {
            double d = (double) input;
            Double.isNaN(d);
            return (float) Math.sin(d * 1.5707963267948966d);
        }
    };
    public static final EasingFunction Linear = new EasingFunction() {
        public float getInterpolation(float input) {
            return input;
        }
    };

    public interface EasingFunction extends TimeInterpolator {
        float getInterpolation(float f);
    }
}
