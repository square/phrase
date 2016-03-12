Phrase - Android string formatting
==================================

[![license](http://img.shields.io/badge/license-apache_2.0-red.svg?style=flat)](https://raw.githubusercontent.com/square/phrase/master/LICENSE.txt) [![maven](https://img.shields.io/maven-central/v/com.squareup.phrase/phrase.svg)](http://maven-repository.com/artifact/com.squareup.phrase) [![build](https://img.shields.io/travis/square/phrase.svg?style=flat)](https://travis-ci.org/square/phrase)

```java
CharSequence formatted = Phrase.from("Hi {first_name}, you are {age} years old.")
  .put("first_name", firstName)
  .put("age", age)
  .format();
```

Send your phrase straight into a TextView:

```java
Phrase.from("Welcome back {user}.")
  .put("user", name)
  .into(textView);
```

Comma-separated lists:
```
CharSequence formattedList = ListPhrase.from(", ")
  .join(1, 2, 3);
// returns "1, 2, 3"
```

English sentence-style lists:
```
ListPhrase listFormatter = ListPhrase.from(
  " and ",
  ", ",
  ", and ");

listFormatter.join(Arrays.asList(1, 2));
// returns "1 and 2"

listFormatter.join(Arrays.asList(1, 2, 3));
// returns "1, 2, and 3"
```

Download
--------

You can download [the latest jar here][jar].

You can also depend on the .jar through Maven:

```xml
<dependency>
  <groupId>com.squareup.phrase</groupId>
  <artifactId>phrase</artifactId>
  <version>(insert latest version)</version>
</dependency>
```

or through Gradle:

```groovy
dependencies {
  compile 'com.squareup.phrase:phrase:(insert latest version)'
}
```

License
-------

    Copyright 2013 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


[jar]: http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.squareup.phrase&a=phrase&v=LATEST
