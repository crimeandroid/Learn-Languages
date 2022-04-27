package com.crimeandroid.game.learnlanguages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;


public class WordAdapter extends ArrayAdapter<Word> {
    //here we are getting the tutoriallist and context
    //so while creating the object of this adapter class we need to give tutoriallist and context
    public WordAdapter(List<Word> languages, Context mCtx) {
        super(mCtx, R.layout.list_item, languages);
    }
  
    //this method will return the list item  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        //getting the layoutinflater  
        ViewHolder holder;  
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_item, null, true);  
        holder = new ViewHolder();  
        //getting text views  
        holder.value = convertView.findViewById(R.id.word);
        holder.wordId = convertView.findViewById(R.id.wordId);
        holder.transcription = convertView.findViewById(R.id.transcription);
        holder.translation = convertView.findViewById(R.id.translation);
        holder.playRus = convertView.findViewById(R.id.playRus);
        holder.play = convertView.findViewById(R.id.play);

        convertView.setTag(holder);

        Word wordObject = this.getItem(position);
        holder.wordId.setText(String.format(Locale.ENGLISH, "%d", wordObject.getId()));
        holder.value.setText(wordObject.getValue());
        holder.transcription.setText(wordObject.getTranscryptCyr());
        holder.translation.setText(wordObject.getTranslate());
        holder.play.setOnClickListener(v -> {
            holder.play.setImageResource(android.R.drawable.ic_media_pause);
            startSound( wordObject.getLocation(),
                () -> holder.play.setImageResource(android.R.drawable.ic_media_play));
        });
        holder.playRus.setOnClickListener(v -> {
            holder.playRus.setImageResource(android.R.drawable.ic_media_pause);
            startSound(wordObject.getLocationRus(),
                () -> holder.playRus.setImageResource(android.R.drawable.ic_media_play));
        });

        return convertView;
    }

    private void startSound(String filename, Runnable... callback) {
        MainActivityOld.startSound(
            filename,
            getContext().getApplicationContext(),
            callback
        );
    }
    static class ViewHolder {
        TextView wordId;
        TextView value;
        TextView transcription;
        TextView translation;
        ImageButton playRus;
        ImageButton play;
    }
}  