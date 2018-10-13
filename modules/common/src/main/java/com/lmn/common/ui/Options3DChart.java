package com.lmn.common.ui;

import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
import com.googlecode.wickedcharts.highcharts.options.SeriesType;

/**
 * Created by MLC on 2017/1/2.
 */
public class Options3DChart extends ChartOptions {
    private Options3d options3d;

    public Options3DChart(SeriesType chartType) {
        super(chartType);
    }

    public Options3d getOptions3d() {
        return options3d;
    }

    public void setOptions3d(Options3d options3d) {
        this.options3d = options3d;
    }
}

