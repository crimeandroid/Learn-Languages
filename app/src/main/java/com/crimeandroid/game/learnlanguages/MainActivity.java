package com.crimeandroid.game.learnlanguages;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState == null) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      RecyclerViewFragment fragment = new RecyclerViewFragment();
      transaction.replace(R.id.sample_content_fragment, fragment);
      transaction.commit();
    }
  }
}