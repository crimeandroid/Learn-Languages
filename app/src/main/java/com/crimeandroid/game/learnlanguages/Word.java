package com.crimeandroid.game.learnlanguages;

import java.util.Locale;

public class Word {

  private static final Locale LOCALE_ru_RU = new Locale("ru", "RU");
  int id;
      String value;
      String transcrypt;
      String transcryptCyr;
      String translate;
      Language language;

  public Word(Language language, int id, String value, String transcrypt, String transcryptCyr,
      String translate) {
    this.id = id;
    this.value = value;
    this.transcrypt = transcrypt;
    this.transcryptCyr = transcryptCyr;
    this.translate = translate;
    this.language = language;
  }

  public int getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public String getTranscrypt() {
    return transcrypt;
  }

  public String getTranscryptCyr() {
    return transcryptCyr;
  }

  public String getTranslate() {
    return translate;
  }

  public Language getLanguage() {
    return language;
  }

  public String getLocation() {
      return getLocation(this.language.getLocale());
  }
  public String getLocationRus() {
      return getLocation(LOCALE_ru_RU);
  }
  private String getLocation(Locale locale) {
      return String.format(Locale.ENGLISH, "%1$s/%1$s-%2$03d.mp3",
          locale, this.getId());
  }
}
