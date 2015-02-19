/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.phrase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Formats a list in a size-dependent way. List separators define how to separate two elements of
 * the list. You can define 3 different separators:
 * <ul>
 * <li>separator for lists with exactly 2 elements (e.g. "first <b>and</b> second")
 * <li>for lists with more than 2 elements, the separator for all but the last element (e.g.
 * "first<b>,</b> second<b>,</b> …")
 * <li>for lists with more than 2 elements, the separator for the second-last and last element
 * (e.g. "second-last<b>, and</b> last")
 * </ul>
 *
 * The {@code join} methods will throw exceptions if the list is null, or
 * contains null or empty ("") elements. They will also throw if {@link Formatter#format(Object)}
 * returns null or an empty string.
 *
 * <p> E.g.
 * <pre>
 *  // Use the same separator for lists of all sizes.
 *  ListPhrase list = ListPhrase.from(", ");
 *  list.join(Arrays.asList("one")) → "one"
 *  list.join(Arrays.asList("one", "two")) → "one, two"
 *  list.join(Arrays.asList("one", "two", "three")) → "one, two, three"
 * </pre>
 * <pre>
 *  // Join English sentence-like lists.
 *  ListPhrase list = ListPhrase.from(
 *    " and ",
 *    ", ",
 *    ", and ");
 *  list.join(Arrays.asList("one")) → "one"
 *  list.join(Arrays.asList("one", "two")) → "one and two"
 *  list.join(Arrays.asList("one", "two", "three")) → "one, two, and three"
 * </pre>
 */
public final class ListPhrase {

  /**
   * Entry point into this API.
   *
   * @param separator separator for all elements
   */
  public static ListPhrase from(@NonNull CharSequence separator) {
    checkNotNull("separator", separator);
    return ListPhrase.from(separator, separator, separator);
  }

  /**
   * Entry point into this API.
   *
   * @param twoElementSeparator separator for 2-element lists
   * @param nonFinalElementSeparator separator for non-final elements of lists with 3 or more
   * elements
   * @param finalElementSeparator separator for final elements in lists with 3 or more elements
   */
  @NonNull public static ListPhrase from(@NonNull CharSequence twoElementSeparator,
      CharSequence nonFinalElementSeparator, CharSequence finalElementSeparator) {
    return new ListPhrase(twoElementSeparator, nonFinalElementSeparator, finalElementSeparator);
  }

  /** Converts a list element to a {@link CharSequence}. */
  public interface Formatter<T> {
    CharSequence format(T item);
  }

  private final CharSequence twoElementSeparator;
  private final CharSequence nonFinalElementSeparator;
  private final CharSequence finalElementSeparator;

  private ListPhrase(@NonNull CharSequence twoElementSeparator,
      @NonNull CharSequence nonFinalElementSeparator, @NonNull CharSequence finalElementSeparator) {

    this.twoElementSeparator = checkNotNull("two-element separator", twoElementSeparator);
    this.nonFinalElementSeparator = checkNotNull("non-final separator", nonFinalElementSeparator);
    this.finalElementSeparator = checkNotNull("final separator", finalElementSeparator);
  }

  /**
   * Join 3 or more objects using {@link Object#toString()} to convert them to {@code Strings}.
   *
   * @throws IllegalArgumentException if any of the list elements are null or empty strings.
   */
  @NonNull public <T> CharSequence join(@NonNull T first, @NonNull T second, @NonNull T... rest) {
    return join(asList(first, second, rest));
  }

  /**
   * Join a list of objects using {@link Object#toString()} to convert them to {@code Strings}.
   *
   * @throws IllegalArgumentException if any of the list elements are null or empty strings.
   */
  @NonNull public <T> CharSequence join(@NonNull Iterable<T> items) {
    checkNotNullOrEmpty(items);
    return join(items, null);
  }

  /**
   * A list of objects, converting them to {@code Strings} by passing them to {@link
   * Formatter#format(Object)}.
   *
   * @throws IllegalArgumentException if any of the list elements are null or empty strings.
   */
  @NonNull public <T> CharSequence join(@NonNull Iterable<T> items,
      @Nullable Formatter<T> formatter) {
    checkNotNullOrEmpty(items);
    return joinIterableWithSize(items, getSize(items), formatter);
  }

  private <T> CharSequence joinIterableWithSize(Iterable<T> items, int size,
      Formatter<T> formatter) {
    switch (size) {
      case 0:
        // This case should be caught by the public join methods and this should never run.
        throw new IllegalStateException("list cannot be empty");
      case 1:
        return formatOrThrow(items.iterator().next(), 0, formatter);
      case 2:
        return joinTwoElements(items, formatter);
      default:
        return joinMoreThanTwoElements(items, size, formatter);
    }
  }

  private <T> CharSequence joinTwoElements(Iterable<T> items, Formatter<T> formatter) {
    StringBuilder builder = new StringBuilder();
    Iterator<T> iterator = items.iterator();

    // Don't need to check hasNext since we know the size.
    builder.append(formatOrThrow(iterator.next(), 0, formatter));
    builder.append(twoElementSeparator);
    builder.append(formatOrThrow(iterator.next(), 1, formatter));
    return builder.toString();
  }

  private <T> CharSequence joinMoreThanTwoElements(Iterable<T> items, int size,
      Formatter<T> formatter) {
    StringBuilder builder = new StringBuilder();
    int secondLastIndex = size - 2;
    Iterator<T> iterator = items.iterator();

    for (int i = 0; i < size; i++) {
      // Don't need to check hasNext since we know the size.
      builder.append(formatOrThrow(iterator.next(), i, formatter));

      if (i < secondLastIndex) {
        builder.append(nonFinalElementSeparator);
      } else if (i == secondLastIndex) {
        builder.append(finalElementSeparator);
      }
    }

    return builder.toString();
  }

  private static int getSize(Iterable<?> iterable) {
    if (iterable instanceof Collection) {
      return ((Collection) iterable).size();
    }

    int size = 0;
    Iterator<?> it = iterable.iterator();
    while (it.hasNext()) {
      size++;
      it.next();
    }
    return size;
  }

  private static <T> List<T> asList(final T first, final T second, final T[] rest) {
    return new AbstractList<T>() {
      @Override public T get(int index) {
        switch (index) {
          case 0:
            return first;
          case 1:
            return second;
          default:
            return rest[index - 2];
        }
      }

      @Override public int size() {
        return rest.length + 2;
      }
    };
  }

  /**
   * Formats {@code item} by passing it to {@code formatter.format()} if {@code formatter} is
   * non-null, else calls {@code item.toString()}. Throws an {@link IllegalArgumentException} if
   * {@code item} is null, and an {@link IllegalStateException} if {@code formatter.format()}
   * returns null.
   */
  private static <T> CharSequence formatOrThrow(T item, int index, Formatter<T> formatter) {
    if (item == null) {
      throw new IllegalArgumentException("list element cannot be null at index " + index);
    }

    CharSequence formatted = formatter == null ? item.toString() : formatter.format(item);

    if (formatted == null) {
      throw new IllegalArgumentException("formatted list element cannot be null at index " + index);
    }
    if (formatted.length() == 0) {
      throw new IllegalArgumentException(
          "formatted list element cannot be empty at index " + index);
    }

    return formatted;
  }

  private static <T> T checkNotNull(String name, T obj) {
    if (obj == null) {
      throw new IllegalArgumentException(name + " cannot be null");
    }
    return obj;
  }

  private static <T> void checkNotNullOrEmpty(Iterable<T> obj) {
    if (obj == null) {
      throw new IllegalArgumentException("list cannot be null");
    }
    if (!obj.iterator().hasNext()) {
      throw new IllegalArgumentException("list cannot be empty");
    }
  }
}
