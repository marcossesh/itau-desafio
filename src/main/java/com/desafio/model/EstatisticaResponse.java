package com.desafio.model;


public class EstatisticaResponse {

    private long count;
    private double sum;
    private double avg;
    private double min;
    private double max;

    public EstatisticaResponse(long count, double sum, double avg, double min, double max) {
        this.count = count;
        this.sum = sum;
        this.avg = avg;
        this.min = min;
        this.max = max;
    }

    public long getCount() {
        return this.count;
    }

    public double getSum() {
        return this.sum;
    }

    public double getAvg() {
        return this.avg;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

}