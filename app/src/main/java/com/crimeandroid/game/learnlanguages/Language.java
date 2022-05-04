package com.crimeandroid.game.learnlanguages;

import java.util.List;
import java.util.Locale;

public class Language {

  int id;
  String name;
  Locale locale;
  List<Word> data;

  public Language(int id, String name, Locale locale,
      List<Word> data) {
    this.id = id;
    this.name = name;
    this.locale = locale;
    this.data = data;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Locale getLocale() {
    return locale;
  }

  public List<Word> getData() {
    return data;
  }
}
