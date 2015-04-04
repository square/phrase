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

import com.squareup.phrase.ListPhrase.Formatter;
import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class) @Config(manifest = Config.NONE)
public class ListPhraseTest {
  private final static String twoElementSeparator = " and ";
  private final static String nonFinalElementSeparator = ", ";
  private final static String finalElementSeparator = ", and ";

  @Rule public ExpectedException thrown = ExpectedException.none();

  ListPhrase listPhrase;

  @Before public void setUp() {
    listPhrase =
        ListPhrase.from(twoElementSeparator, nonFinalElementSeparator, finalElementSeparator);
  }

  @Test public void fromNullThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("separator cannot be null");
    ListPhrase.from(null);
  }

  @Test public void fromNullTwoElementThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("two-element separator cannot be null");
    ListPhrase.from(null, ",", ",");
  }

  @Test public void fromNullNonFinalElementThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("non-final separator cannot be null");
    ListPhrase.from(",", null, ",");
  }

  @Test public void fromNullFinalElementThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("final separator cannot be null");
    ListPhrase.from(",", ",", null);
  }

  @Test public void joinEmptyListThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("list cannot be empty");
    listPhrase.join(Collections.emptyList());
  }

  @Test public void joinEmptyStringThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("formatted list element cannot be empty at index 0");
    listPhrase.join(asList(""));
  }

  @Test public void joinOneItemReturnsIdentity() {
    assertThat(listPhrase.join(asList("one"))).isEqualTo("one");
  }

  @Test public void joinTwoItemsUsesTwoElementSeparator() {
    assertThat(listPhrase.join(asList("one", "two"))).isEqualTo("one and two");
  }

  @Test public void joinThreeItemsUsesThreeElementSeparator() {
    assertThat(listPhrase.join("one", "two", "three")).isEqualTo("one, two, and three");
  }

  @Test public void joinMoreThanThreeItems() {
    assertThat(listPhrase.join("one", "two", "three", "four")).isEqualTo(
        "one, two, three, and four");
  }

  @Test public void joinNullIterableNullFormatter() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("list cannot be null");

    listPhrase.join(null, null);
  }

  @Test public void joinNullIterableNonNullFormatter() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("list cannot be null");

    listPhrase.join(null, new Formatter<Object>() {
      @Override public CharSequence format(Object item) {
        return "hi";
      }
    });
  }

  @Test public void joinNullElementInIterableThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("list element cannot be null at index 1");

    listPhrase.join(asList("foo", null));
  }

  @Test public void joinNullElementInIterableWithFormatterThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("list element cannot be null at index 1");

    listPhrase.join(asList("foo", null), new Formatter<String>() {
      @Override public CharSequence format(String item) {
        return "bar";
      }
    });
  }

  @Test public void joinWithFormatterReturningNullThrows() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("formatted list element cannot be null at index 0");

    listPhrase.join(asList("foo"), new Formatter<String>() {
      @Override public CharSequence format(String item) {
        return null;
      }
    });
  }

  @Test public void joinNonCharSequenceUsesToString() {
    assertThat(listPhrase.join(1, 2, 3)).isEqualTo("1, 2, and 3");
  }

  @Test public void joinWithFormatter() {
    Formatter<Integer> formatter = new Formatter<Integer>() {
      @Override public CharSequence format(Integer item) {
        return String.format("0x%d", item);
      }
    };

    assertThat(listPhrase.join(asList(1, 2, 3), formatter)).isEqualTo("0x1, 0x2, and 0x3");
  }
}
