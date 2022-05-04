package com.crimeandroid.game.learnlanguages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class WordRecycleViewAdapter extends
    RecyclerView.Adapter<WordRecycleViewAdapter.ViewHolder> {

  private static final String TAG = "WordRecycleViewAdapter";
  private final Context mContext;
  private final Data data;
  private final Locale localeLearn;
  private final Locale localeBase;
  private int currentPosition = RecyclerView.NO_POSITION;

  /**
   * Initialize the dataset of the Adapter.
   *
   * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
   */
  public WordRecycleViewAdapter(Data dataSet, Context context, Locale learn, Locale base) {
    this.data = dataSet;
    this.mContext = context;
    this.localeLearn = learn;
    this.localeBase = base;
  }

  public static Word findWord(int id, Language language) {
    for (Word word : language.getData()) {
      if (word.getId() == id) {
        return word;
      }
    }
    throw new IllegalArgumentException("id = " + id);
  }

  public static Language findLanguageByLocale(Locale locale, List<Language> languages) {
    for (Language language : languages) {
      if (language.getLocale().toString().equalsIgnoreCase(locale.toString())) {
        return language;
      }
    }
    throw new IllegalArgumentException(locale.toString());
  }

  public Word getItem(int position) {
    return findLanguageByLocale(this.localeLearn, this.data.languages).getData().get(position);
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.list_item, viewGroup, false);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int position) {
    int currentPosition = this.currentPosition;
    viewHolder.itemView.setSelected(currentPosition == position);
    Word wordObject = findLanguageByLocale(this.localeLearn, this.data.getLanguages()).getData()
        .get(position);
    viewHolder.wordId.setText(String.format(Locale.ENGLISH, "%d", wordObject.getId()));
    viewHolder.value.setText(wordObject.getValue());
    viewHolder.transcription.setText(wordObject.getTranscryptCyr());
    viewHolder.translation.setText(wordObject.getTranslate());
    viewHolder.play.setOnClickListener(
        v -> setOnClick(viewHolder, position, viewHolder.play, wordObject.getLocation()));
    viewHolder.playRus.setOnClickListener(
        v -> setOnClick(viewHolder, position, viewHolder.playRus, wordObject.getLocationRus()));
    viewHolder.itemView.setOnClickListener(v -> this.setCurrentPosition(position));
  }

  private void setOnClick(ViewHolder viewHolder, int position, ImageButton playButton,
      String location) {
    startSound(location);
    setCurrentPosition(position);
  }

  @Override
  public int getItemCount() {
    return findLanguageByLocale(localeLearn, this.data.getLanguages()).getData().size();
  }

  private void startSound(String filename, Runnable... callback) {
    RecyclerViewFragment.startSound(
        filename,
        mContext.getApplicationContext(),
        callback
    );
  }

  public void setCurrentPosition(int currentPosition) {
    notifyDataSetChanged();
    this.currentPosition = currentPosition;
    notifyItemChanged(currentPosition);
  }

  /**
   * Provide a reference to the type of views that you are using (custom ViewHolder)
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView wordId;
    TextView value;
    TextView transcription;
    TextView translation;
    ImageButton playRus;
    ImageButton play;

    public ViewHolder(View v) {
      super(v);
      v.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
        }
      });
      this.value = v.findViewById(R.id.word);
      this.wordId = v.findViewById(R.id.wordId);
      this.transcription = v.findViewById(R.id.transcription);
      this.translation = v.findViewById(R.id.translation);
      this.playRus = v.findViewById(R.id.playRus);
      this.play = v.findViewById(R.id.play);
    }

    public TextView getWordId() {
      return wordId;
    }

    public TextView getValue() {
      return value;
    }

    public TextView getTranscription() {
      return transcription;
    }

    public TextView getTranslation() {
      return translation;
    }

    public ImageButton getPlayRus() {
      return playRus;
    }

    public ImageButton getPlay() {
      return play;
    }
  }
}