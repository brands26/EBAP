package com.beliautopart.beliautopart.model;

/**
 * Created by Brandon Pratama on 11/06/2016.
 */
public class KomplainModel {
    private String id;
    private String nomor;
    private String tanggal;
    private String pesan;
    private String JobId;
    private String OrderId;


    public KomplainModel() {
    }

    public KomplainModel(String id, String nomor, String tanggal, String pesan) {
        this.id = id;
        this.nomor = nomor;
        this.tanggal = tanggal;
        this.pesan = pesan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }
}
