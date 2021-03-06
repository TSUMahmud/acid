package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;

public class ConstraintHorizontalLayout extends ConstraintWidgetContainer {
    private ContentAlignment mAlignment = ContentAlignment.MIDDLE;

    public enum ContentAlignment {
        BEGIN,
        MIDDLE,
        END,
        TOP,
        VERTICAL_MIDDLE,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public ConstraintHorizontalLayout() {
    }

    public ConstraintHorizontalLayout(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ConstraintHorizontalLayout(int width, int height) {
        super(width, height);
    }

    public void addToSolver(LinearSystem system) {
        ConstraintAnchor.Strength strength;
        if (this.mChildren.size() != 0) {
            ConstraintHorizontalLayout constraintHorizontalLayout = this;
            int mChildrenSize = this.mChildren.size();
            for (int i = 0; i < mChildrenSize; i++) {
                ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
                if (constraintHorizontalLayout != this) {
                    widget.connect(ConstraintAnchor.Type.LEFT, (ConstraintWidget) constraintHorizontalLayout, ConstraintAnchor.Type.RIGHT);
                    constraintHorizontalLayout.connect(ConstraintAnchor.Type.RIGHT, widget, ConstraintAnchor.Type.LEFT);
                } else {
                    ConstraintAnchor.Strength strength2 = ConstraintAnchor.Strength.STRONG;
                    if (this.mAlignment == ContentAlignment.END) {
                        strength = ConstraintAnchor.Strength.WEAK;
                    } else {
                        strength = strength2;
                    }
                    widget.connect(ConstraintAnchor.Type.LEFT, (ConstraintWidget) constraintHorizontalLayout, ConstraintAnchor.Type.LEFT, 0, strength);
                }
                widget.connect(ConstraintAnchor.Type.TOP, (ConstraintWidget) this, ConstraintAnchor.Type.TOP);
                widget.connect(ConstraintAnchor.Type.BOTTOM, (ConstraintWidget) this, ConstraintAnchor.Type.BOTTOM);
                constraintHorizontalLayout = widget;
            }
            if (constraintHorizontalLayout != this) {
                ConstraintAnchor.Strength strength3 = ConstraintAnchor.Strength.STRONG;
                if (this.mAlignment == ContentAlignment.BEGIN) {
                    strength3 = ConstraintAnchor.Strength.WEAK;
                }
                constraintHorizontalLayout.connect(ConstraintAnchor.Type.RIGHT, (ConstraintWidget) this, ConstraintAnchor.Type.RIGHT, 0, strength3);
            }
        }
        super.addToSolver(system);
    }
}
