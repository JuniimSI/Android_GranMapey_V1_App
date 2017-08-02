package com.example.juniorf.granmapey.MODEL;

import android.content.Context;

/**
 * Created by juniorf on 17/12/16.
 */

public class MyLocation {
    private int id;
    private String nome;
    private String telefone;
    private double lat;
    private double lng;
    private int tipo;
    private String email;


    public MyLocation() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MyLocation(double lat, double lng, String nome, String telefone, int tipo, String email) {
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.nome = nome;
        this.telefone = telefone;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
