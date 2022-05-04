package com.crimeandroid.game.learnlanguages;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.MediaPlayer;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a {@link
 * GridLayoutManager}.
 */
public class RecyclerViewFragment extends Fragment {

  private static final String TAG = "RecyclerViewFragment";
  protected RecyclerView mRecyclerView;
  protected WordRecycleViewAdapter mAdapter;
  protected RecyclerView.LayoutManager mLayoutManager;
  protected Data data;
  private int position = -1;
  private boolean playerActive = false;
  private Locale localeLearn;
  private Locale localeBase;

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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    localeLearn = new Locale("el", "GR");
    localeBase = new Locale("ru", "RU");
    initDataset();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
    rootView.setTag(TAG);
    mRecyclerView = rootView.findViewById(R.id.recyclerView);
    mLayoutManager = new LinearLayoutManager(getActivity());
    setRecyclerViewLayoutManager();
    mAdapter = new WordRecycleViewAdapter(data, getContext(), localeLearn, localeBase);
    mRecyclerView.setAdapter(mAdapter);
    return rootView;
  }

  /**
   * Set RecyclerView's LayoutManager to the one given.
   */
  public void setRecyclerViewLayoutManager() {
    int scrollPosition = 0;
    if (mRecyclerView.getLayoutManager() != null) {
      scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
          .findFirstCompletelyVisibleItemPosition();
    }
    mLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.scrollToPosition(scrollPosition);
  }

  /**
   * Generates Strings for RecyclerView's adapter. This data would usually come from a local content
   * provider or remote server.
   */
  private void initDataset() {
    this.data = new Data(new ArrayList<>());
    StringBuilder sb = new StringBuilder();
    try (InputStream is = requireContext().getApplicationContext().getAssets().open("data.json");
        BufferedReader r = new BufferedReader(new InputStreamReader(is))) {
      String s;
      while ((s = r.readLine()) != null) {
        sb.append(s);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      JSONObject obj = new JSONObject(sb.toString());
      JSONArray languagesArray = obj.getJSONArray("languages");
      for (int languageId = 0; languageId < languagesArray.length(); languageId++) {
        JSONObject languageObject = languagesArray.getJSONObject(languageId);
        data.getLanguages().add(getLanguage(languageObject));
      }
      Language baseLanguage = WordRecycleViewAdapter
          .findLanguageByLocale(localeBase, data.languages);
      for (Language language : data.getLanguages()) {
        if (language.getLocale().toString().equalsIgnoreCase(localeBase.toString())) {
          continue;
        }
        for (Word word : language.getData()) {
          word.setTranslate(WordRecycleViewAdapter.findWord(word.getId(), baseLanguage).getValue());
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private Language getLanguage(JSONObject languageObject) throws JSONException {
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
      int wordId = data.getInt("word_id");
      Word word = new Word(
          language,
          wordId,
          data.getString("value"),
          data.getString("transcrypt"),
          data.getString("transcrypt_cyr"),
          ""
      );
      wordList.add(word);
    }
    return language;
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.playAll: {
        playerActive = !playerActive;
        if (playerActive) {
          menuItem.setIcon(android.R.drawable.ic_media_pause);
        } else {
          menuItem.setIcon(android.R.drawable.ic_media_ff);
        }
        next();
        break;
      }
    }
    return true;
  }

  private void next() {
    if (!playerActive) {
      return;
    }
    position++;
    if (position < 0 || position >= mAdapter.getItemCount()) {
      position = 0;
    }
    mAdapter.setCurrentPosition(position);
    mLayoutManager.scrollToPosition(position);
    Word item = mAdapter.getItem(position);
    String locationCurrent = item.getLocation();
    String locationNext = item.getLocationRus();
    startSound(locationCurrent, requireContext(),
        () -> startSound(locationNext, requireContext(), this::next));
  }
}