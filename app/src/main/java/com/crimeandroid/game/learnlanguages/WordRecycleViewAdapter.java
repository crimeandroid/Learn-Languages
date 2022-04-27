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

  private final List<Word> mDataSet;
  // END_INCLUDE(recyclerViewOnCreateViewHolder)
  private int currentPosition = RecyclerView.NO_POSITION;

  // BEGIN_INCLUDE(recyclerViewSampleViewHolder)

  /**
   * Initialize the dataset of the Adapter.
   *
   * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
   */
  public WordRecycleViewAdapter(List<Word> dataSet, Context context) {
    this.mDataSet = dataSet;
    this.mContext = context;
  }
  // END_INCLUDE(recyclerViewSampleViewHolder)

  public Word getItem(int position) {
    return mDataSet.get(position);
  }

  // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
  // Create new views (invoked by the layout manager)
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    // Create a new view.
    View v = LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.list_item, viewGroup, false);

    return new ViewHolder(v);
  }

  // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
  // Replace the contents of a view (invoked by the layout manager)
  @Override
  public void onBindViewHolder(ViewHolder viewHolder, final int position) {

    int currentPosition = this.currentPosition;
    viewHolder.itemView.setSelected(currentPosition == position);

    // Get element from your dataset at this position and replace the contents of the view
    // with that element
    Word wordObject = this.mDataSet.get(position);
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
/*    playButton.setImageResource(android.R.drawable.ic_media_pause);
    this.notifyItemChanged(position);*/

    startSound(location/*,
        () -> {
          viewHolder.play.setImageResource(android.R.drawable.ic_media_play);
          this.notifyItemChanged(position);
        }*/);
    setCurrentPosition(position);
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return mDataSet.size();
  }
  // END_INCLUDE(recyclerViewOnBindViewHolder)

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
      // Define click listener for the ViewHolder's View.
      v.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
        }
      });
      //getting text views
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