package com.crimeandroid.game.learnlanguages;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.MediaPlayer;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivityOld extends FragmentActivity {

  ListView listView;
  //the tutorial list where we will store all the tutorial objects after parsing json
  List<Word> wordList;
  private int position = -1;
  private boolean playerActive = false;

  public static void startSound(String filename, Context applicationContext,
      Runnable... callback) {
    MediaPlayer mediaPlayer = new MediaPlayer();
    set(mediaPlayer);
    try {
      AssetFileDescriptor afd = applicationContext.getResources()
          .getAssets().openFd(filename);
      mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
      mediaPlayer.prepare();
    } catch (IOException e) {
      e.printStackTrace();
    }
    mediaPlayer.start();
    mediaPlayer.setOnCompletionListener(mp -> {
      mp.release();
      if (callback != null && callback.length > 0 && callback[0] != null) {
        callback[0].run();
      }
    });
  }

  private static void set(MediaPlayer mediaPlayer) {
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      mediaPlayer.setAudioAttributes(
          new Builder()
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .build()
      );
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    listView = findViewById(R.id.listView);
    wordList = new ArrayList<>();
    //this method will fetch and parse the data
    loadWordsList();
  }

  private void loadWordsList() {
    //getting the progressbar
    final ProgressBar progressBar = findViewById(R.id.progressBar);
    progressBar.setVisibility(View.VISIBLE);
//    InputStream is ;
    StringBuilder sb = new StringBuilder();
    try (InputStream is = this.getAssets().open("data.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
      String s = null;
      while ((s = r.readLine()) != null) {
        sb.append(s);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    //creating a string request to send request to the url

    //hiding the progressbar after completion
    progressBar.setVisibility(View.INVISIBLE);

    try {
      //getting the whole json object from the response
      JSONObject obj = new JSONObject(sb.toString());

      //we have the array named tutorial inside the object
      //so here we are getting that json array
      JSONArray languagesArray = obj.getJSONArray("languages");
      List<Word> rusWords = getLanguageWords(languagesArray.getJSONObject(0), null);
      //now looping through all the elements of the json array
      for (int languageId = 1; languageId < languagesArray.length(); languageId++) {
        //getting the json object of the particular index inside the array
        JSONObject languageObject = languagesArray.getJSONObject(languageId);
        this.wordList.addAll(getLanguageWords(languageObject, rusWords));
      }
      //creating custom adapter object
      WordAdapter adapter = new WordAdapter(wordList, getApplicationContext());

      //adding the adapter to listview
      listView.setAdapter(adapter);

    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return true;
  }

  private List<Word> getLanguageWords(JSONObject languageObject,
      List<Word> rusWords) throws JSONException {
    List<Word> wordList = new ArrayList<>();
    JSONArray wordsArray = languageObject.getJSONArray("data");
    Language language = new Language(
        languageObject.getInt("id"),
        languageObject.getString("name"),
        new Locale(languageObject.getString("locale"), languageObject.getString("region")),
        wordList
    );
    for (int j = 0; j < wordsArray.length(); j++) {
      JSONObject data = wordsArray.getJSONObject(j);
      //creating a word object and giving them the values from json object
      int wordId = data.getInt("word_id");
      Word rusWord = null;
      if (rusWords != null) {
        for (Word w : rusWords) {
          if (w.id == wordId) {
            rusWord = w;
            break;
          }
        }
      }
      Word word = new Word(
          language,
          wordId,
          data.getString("value"),
          data.getString("transcrypt"),
          data.getString("transcrypt_cyr"),
          rusWord != null ? rusWord.getValue() : ""

      );
      wordList.add(word);
    }
    return wordList;
  }

  public void playAll(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.playAll: {
        playerActive = !playerActive;
        if(playerActive) {
          menuItem.setIcon(android.R.drawable.ic_media_pause);
        }else {
          menuItem.setIcon(android.R.drawable.ic_media_ff);
        }
        next();
        break;
      }
    }
  }

  private void next() {
    if (!playerActive) {
      return;
    }
    ListAdapter adapter = listView.getAdapter();
    position++;
    if (position < 0 || position >= adapter.getCount()) {
      position = 0;
    }
    listView.smoothScrollToPosition(position);
    Word item = (Word) adapter.getItem(position);
    String locationCurrent = item.getLocation();
    String locationNext = item.getLocationRus();
    startSound(locationCurrent, getApplicationContext(),
        () -> startSound(locationNext, getApplicationContext(), this::next));
  }
}