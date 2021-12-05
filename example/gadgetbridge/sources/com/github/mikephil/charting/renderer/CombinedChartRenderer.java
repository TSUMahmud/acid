package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.util.Log;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ViewPortHandler;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CombinedChartRenderer extends DataRenderer {
    protected WeakReference<Chart> mChart;
    protected List<Highlight> mHighlightBuffer = new ArrayList();
    protected List<DataRenderer> mRenderers = new ArrayList(5);

    public CombinedChartRenderer(CombinedChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        this.mChart = new WeakReference<>(chart);
        createRenderers();
    }

    public void createRenderers() {
        this.mRenderers.clear();
        CombinedChart chart = (CombinedChart) this.mChart.get();
        if (chart != null) {
            for (CombinedChart.DrawOrder order : chart.getDrawOrder()) {
                int i = C05761.f83x2dab6d3b[order.ordinal()];
                if (i != 1) {
                    if (i != 2) {
                        if (i != 3) {
                            if (i != 4) {
                                if (i == 5 && chart.getScatterData() != null) {
                                    this.mRenderers.add(new ScatterChartRenderer(chart, this.mAnimator, this.mViewPortHandler));
                                }
                            } else if (chart.getCandleData() != null) {
                                this.mRenderers.add(new CandleStickChartRenderer(chart, this.mAnimator, this.mViewPortHandler));
                            }
                        } else if (chart.getLineData() != null) {
                            this.mRenderers.add(new LineChartRenderer(chart, this.mAnimator, this.mViewPortHandler));
                        }
                    } else if (chart.getBubbleData() != null) {
                        this.mRenderers.add(new BubbleChartRenderer(chart, this.mAnimator, this.mViewPortHandler));
                    }
                } else if (chart.getBarData() != null) {
                    this.mRenderers.add(new BarChartRenderer(chart, this.mAnimator, this.mViewPortHandler));
                }
            }
        }
    }

    /* renamed from: com.github.mikephil.charting.renderer.CombinedChartRenderer$1 */
    static /* synthetic */ class C05761 {

        /* renamed from: $SwitchMap$com$github$mikephil$charting$charts$CombinedChart$DrawOrder */
        static final /* synthetic */ int[] f83x2dab6d3b = new int[CombinedChart.DrawOrder.values().length];

        static {
            try {
                f83x2dab6d3b[CombinedChart.DrawOrder.BAR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f83x2dab6d3b[CombinedChart.DrawOrder.BUBBLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f83x2dab6d3b[CombinedChart.DrawOrder.LINE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f83x2dab6d3b[CombinedChart.DrawOrder.CANDLE.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f83x2dab6d3b[CombinedChart.DrawOrder.SCATTER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public void initBuffers() {
        for (DataRenderer renderer : this.mRenderers) {
            renderer.initBuffers();
        }
    }

    public void drawData(Canvas c) {
        for (DataRenderer renderer : this.mRenderers) {
            renderer.drawData(c);
        }
    }

    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        Log.e(Chart.LOG_TAG, "Erroneous call to drawValue() in CombinedChartRenderer!");
    }

    public void drawValues(Canvas c) {
        for (DataRenderer renderer : this.mRenderers) {
            renderer.drawValues(c);
        }
    }

    public void drawExtras(Canvas c) {
        for (DataRenderer renderer : this.mRenderers) {
            renderer.drawExtras(c);
        }
    }

    public void drawHighlighted(Canvas c, Highlight[] indices) {
        int dataIndex;
        Chart chart = (Chart) this.mChart.get();
        if (chart != null) {
            for (DataRenderer renderer : this.mRenderers) {
                ChartData data = null;
                if (renderer instanceof BarChartRenderer) {
                    data = ((BarChartRenderer) renderer).mChart.getBarData();
                } else if (renderer instanceof LineChartRenderer) {
                    data = ((LineChartRenderer) renderer).mChart.getLineData();
                } else if (renderer instanceof CandleStickChartRenderer) {
                    data = ((CandleStickChartRenderer) renderer).mChart.getCandleData();
                } else if (renderer instanceof ScatterChartRenderer) {
                    data = ((ScatterChartRenderer) renderer).mChart.getScatterData();
                } else if (renderer instanceof BubbleChartRenderer) {
                    data = ((BubbleChartRenderer) renderer).mChart.getBubbleData();
                }
                if (data == null) {
                    dataIndex = -1;
                } else {
                    dataIndex = ((CombinedData) chart.getData()).getAllData().indexOf(data);
                }
                this.mHighlightBuffer.clear();
                for (Highlight h : indices) {
                    if (h.getDataIndex() == dataIndex || h.getDataIndex() == -1) {
                        this.mHighlightBuffer.add(h);
                    }
                }
                List<Highlight> list = this.mHighlightBuffer;
                renderer.drawHighlighted(c, (Highlight[]) list.toArray(new Highlight[list.size()]));
            }
        }
    }

    public DataRenderer getSubRenderer(int index) {
        if (index >= this.mRenderers.size() || index < 0) {
            return null;
        }
        return this.mRenderers.get(index);
    }

    public List<DataRenderer> getSubRenderers() {
        return this.mRenderers;
    }

    public void setSubRenderers(List<DataRenderer> renderers) {
        this.mRenderers = renderers;
    }
}
