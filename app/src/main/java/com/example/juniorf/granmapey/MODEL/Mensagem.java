package com.example.juniorf.granmapey.MODEL;

/**
 * Created by juniorf on 24/05/17.
 */

public class Mensagem {

    private Integer id;
    private String texto;
    private String emailOrigem;
    private String emailDestino;
    private String local;

    public Mensagem() {
    }

    public Mensagem(Integer id, String texto, String emailOrigem, String emailDestino, String local) {
        this.id = id;
        this.texto = texto;
        this.emailOrigem = emailOrigem;
        this.emailDestino = emailDestino;
        this.local = local;
    }

    public Integer getId() { return id;}
    public void setId(Integer id) { this.id = id;     }
    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }
    public String getEmailOrigem() { return emailOrigem; }
    public void setEmailOrigem(String emailOrigem) { this.emailOrigem = emailOrigem; }
    public String getEmailDestino() { return emailDestino; }
    public void setEmailDestino(String emailDestino) { this.emailDestino = emailDestino; }
    public String getLocal() {
        return local;
    }
    public void setLocal(String local) {
        this.local = local;
    }

}
