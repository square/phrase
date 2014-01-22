Phrase - Android string formatting
==================================

```java
CharSequence formatted = Phrase.from("Hi {first_name}, you are {age} years old.")
  .put("first_name", firstName)
  .put("age", age)
  .format();
```

Read more about it in this [blog post](http://corner.squareup.com/2014/01/phrase.html).

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
