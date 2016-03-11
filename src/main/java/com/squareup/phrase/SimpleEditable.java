package com.squareup.phrase;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;

import static java.util.regex.Matcher.quoteReplacement;
import static java.util.regex.Pattern.quote;

/**
 * Provides basic support for replacing tokens with values without spans. This
 * is used when {@link Phrase#setSpanSupportEnabled(boolean)} is turned off.
 */
final class SimpleEditable implements Editable {
  private String text;

  public SimpleEditable(CharSequence text) {
    this(text.toString());
  }

  public SimpleEditable(String text) {
    this.text = text;
  }

  @Override public Editable replace(int st, int en, CharSequence source, int start, int end) {
    text = text.replaceFirst(quote(text.substring(st, en)), quoteReplacement(source.toString()));
    return this;
  }

  @Override public Editable replace(int st, int en, CharSequence text) {
    return replace(st, en, text, 0, text.length());
  }

  @Override public Editable insert(int where, CharSequence text, int start, int end) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  @Override public Editable insert(int where, CharSequence text) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  @Override public Editable delete(int st, int en) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  @Override public Editable append(CharSequence text) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  @Override public Editable append(CharSequence text, int start, int end) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  @Override public Editable append(char text) {
    throw new UnsupportedOperationException("Not implemented.");
  }

  @Override public void clear() {
  }

  @Override public void clearSpans() {
  }

  @Override public void setFilters(InputFilter[] filters) {
  }

  @Override public InputFilter[] getFilters() {
    return new InputFilter[0];
  }

  @Override public void getChars(int start, int end, char[] dest, int destoff) {
  }

  @Override public void setSpan(Object what, int start, int end, int flags) {
  }

  @Override public void removeSpan(Object what) {
  }

  @Override public <T> T[] getSpans(int start, int end, Class<T> type) {
    return null;
  }

  @Override public int getSpanStart(Object tag) {
    return 0;
  }

  @Override public int getSpanEnd(Object tag) {
    return 0;
  }

  @Override public int getSpanFlags(Object tag) {
    return 0;
  }

  @Override public int nextSpanTransition(int start, int limit, Class type) {
    return 0;
  }

  @Override public int length() {
    return text.length();
  }

  @Override public char charAt(int index) {
    return text.charAt(index);
  }

  @Override public CharSequence subSequence(int start, int end) {
    return text.subSequence(start, end);
  }

  @NonNull @Override public String toString() {
    return text;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;

    if (o.getClass() == String.class) {
      return text.equals(o);
    }

    if (getClass() != o.getClass()) return false;

    SimpleEditable that = (SimpleEditable) o;

    return text.equals(that.text);
  }

  @Override public int hashCode() {
    return text.hashCode();
  }
}
