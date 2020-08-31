package com.tranbaquan.elasticadmin.model;

import java.util.ArrayList;
import java.util.List;

public class ChartData {
    private List<String> labels;
    private List<Long> data;

    public ChartData() {
        labels = new ArrayList<>();
        data = new ArrayList<>();
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Long> getData() {
        return data;
    }

    public void setData(List<Long> data) {
        this.data = data;
    }
}
