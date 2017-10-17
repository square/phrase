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

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.squareup.phrase.Phrase.from;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class PhraseTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  @Test public void emptyStringFormatsToItself() {
    assertThat(from("").format().toString()).isEqualTo("");
  }

  @Test public void trivialStringFormatsToItself() {
    assertThat(from("Hello").format().toString()).isEqualTo("Hello");
  }

  @Test public void toStringReturnsThePattern() {
    assertThat(from("hello {name}").toString()).isEqualTo("hello {name}");
  }

  @Test public void puttingNullValueThrowsException() {
    try {
      from("hi {name}").put("name", null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Null value for 'name'");
    }
  }

  @Test public void singleCurlyBraceIsAMistake() {
    try {
      from("{");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test public void twoLeftCurlyBracesFormatAsSingleCurlyBrace() {
    assertThat(from("{{").format().toString()).isEqualTo("{");
  }

  @Test public void testSimpleSuccessfulSubstitution() {
    assertThat(from("hi {name}").put("name", "Eric").format().toString()).isEqualTo("hi Eric");
  }

  @Test public void loneCurlyBraceIsAMistake() {
    try {
      from("hi { {age}.");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test public void ignoresTokenNextToEscapedBrace() {
    assertThat(from("hi {{name} {name}").put("name", "Bubba").format().toString()).isEqualTo(
        "hi {name} Bubba");
  }

  @Test public void canEscapeCurlyBracesImmediatelyBeforeKey() {
    assertThat(from("you are {{{name}").put("name", "Steve").format().toString()).isEqualTo(
        "you are {Steve");
  }

  @Test public void canReplaceKeysAndReusePhraseInstance() {
    Phrase pattern = from("hi {name}.");
    assertThat(pattern.put("name", "George").format().toString()).isEqualTo("hi George.");
    pattern.put("name", "Abe");
    assertThat(pattern.put("name", "Abe").format().toString()).isEqualTo("hi Abe.");
  }

  @Test public void patternsCanHaveSeveralKeys() {
    assertThat(from("hi {name}, you are {age} years old. {name}").put("name", "Abe")
        .put("age", 20)
        .format()
        .toString()).isEqualTo("hi Abe, you are 20 years old. Abe");
  }

  @Test public void putOptionalIgnoresKey() {
    assertThat(from("Hello").putOptional("key", "value").format().toString()).isEqualTo("Hello");
  }

  @Test public void putOptionalWorksIfKeyIsPresent() {
    assertThat(from("Hello {name}").putOptional("name", "Eric").format().toString())
        .isEqualTo("Hello Eric");
  }

  @Test public void putFailUppercaseNotAllowedMiddle() {
    try{
      from("Hello {aName}").putOptional("Name", "Eric").format();
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()) //
          .isEqualTo("Unexpected character 'N'; expecting lower case a-z, '_', or '}'");
    }
  }

  private Phrase gender = from("{gender}");

  @Test
  public void formatFailsFastWhenKeysAreMissing() {
    try {
      gender.format();
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test public void putFailsFastWhenPuttingUnknownKey() {
    try {
      gender.put("bogusKey", "whatever");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test public void emptyTokenFailsFast() {
    try {
      from("illegal {} pattern");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test public void illegalStartOfTokenCharactersFailFast() {
    try {
      from("blah {NoUppercaseAllowed}");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).isEqualTo("Unexpected first character 'N'; must be lower case a-z.");
    }
  }

  @Test public void tokensCanHaveUnderscores() {
    assertThat(from("{first_name}").put("first_name", "Eric").format().toString()).isEqualTo(
        "Eric");
  }

  @Test public void keysCannotStartWithUnderscore() {
    try {
      from("{_foo}");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
    }
  }

  @Test public void testRetainsSpans() {
    SpannableStringBuilder ssb =
        new SpannableStringBuilder("Hello {name}, you are {age} years old.");
    ssb.setSpan("bold", 5, 28, 0);

    CharSequence formatted = Phrase.from(ssb).put("name",
        "Abe").put("age", 20).format();
    assertThat(formatted.toString()).isEqualTo("Hello Abe, you are 20 years old.");
    assertThat(formatted).isInstanceOf(Spannable.class);
  }

  @Test public void intoSetsTargetText() {
    Context context = RuntimeEnvironment.application;
    TextView textView = new TextView(context);

    Phrase.from("Hello {user}!").put("user", "Eric").into(textView);
    CharSequence actual = textView.getText().toString();

    assertThat(actual.toString()).isEqualTo("Hello Eric!");
  }

  @Test public void intoNullFailsFast() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("TextView must not be null.");
    Phrase.from("Hello {user}!").put("user", "Eric").into(null);
  }
}
