package com.example.juniorf.granmapey.ADAPTER;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.juniorf.granmapey.LOGIN.LogInActivity;
import com.example.juniorf.granmapey.MODEL.Mensagem;
import com.example.juniorf.granmapey.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juniorf on 01/08/17.
 */

public class MessageAdapter extends BaseAdapter {

    private final List<Mensagem> messages;
    private final Activity act;
    public MessageAdapter(List<Mensagem> messages, Activity activity) {
        this.messages = messages;
        this.act = activity;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        Mensagem c = messages.get(position);
        return c.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = act.getLayoutInflater()
        .inflate(R.layout.list_item, parent, false);
        Mensagem mensagem = messages.get(position);
        //pegando as referências das Views
        TextView email = (TextView)
                view.findViewById(R.id.tvEmail);
        TextView message = (TextView)
                view.findViewById(R.id.tvMessage);
        ImageView imagem = (ImageView)
                view.findViewById(R.id.ivPerfil);

        //populando as Views
        email.setText(mensagem.getEmailOrigem());
        message.setText(mensagem.getTexto());
        imagem.setImageResource(R.drawable.cast_ic_notification_connecting);

        return view;
    }
/*
    public MessageAdapter(@NonNull Context context, ArrayList<Mensagem> mensagemArrayList) {
        super(context, R.layout.list_item, mensagemArrayList);
        this.context = context;
        this.mensagemArrayList = mensagemArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView =  inflater.inflate(R.layout.list_item, parent, false);

    }*/
}
