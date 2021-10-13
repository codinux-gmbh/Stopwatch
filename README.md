# Stopwatch

Simple stopwatch to measure and format durations.

## Setup

### Maven

```
<dependency>
    <groupId>net.codinux.util</groupId>
    <artifactId>stopwatch</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```
implementation 'net.codinux.util:stopwatch:1.0.0'
```


## Usage

### Java

Static wrapper around Stopwatch:
```java
    // returns the elapsed time
    Duration measuredDuration = Stopwatch.measureDuration(() -> myTask());

    // returns elapsed time formatted
    String formattedDuration = Stopwatch.formatDuration(() -> myTask());

    // logs elapsed time to class' slf4j logger in format: "<action> <formatted_duration>"
    Stopwatch.logDuration("My important task", () -> myTask()); // see console log output

    // of course you can also specify the (slf4j) logger to log to
    Stopwatch.logDuration("Other task", LoggerFactory.getLogger("Task logger"), () -> myTask());

    // logs elapsed time and returns task's result
    long heavyCalculationResult = Stopwatch.logDuration("Task that returns a result", () -> {
        myTask(); // mimic heavy calculation
        return 123L; // return result to caller
    });
```

Stopwatch instance methods:
```java
    // returns the elapsed time
    Stopwatch measureDuration = new Stopwatch(); // will automatically be created in started state
    myTask();
    Duration measuredDuration = measureDuration.stop();

    // returns elapsed time formatted
    Stopwatch formatDuration = new Stopwatch();
    myTask();
    String formattedDuration = formatDuration.stopAndFormat();

    // logs elapsed time to class' slf4j logger in format: "<action> <formatted_duration>"
    Stopwatch logDuration = new Stopwatch();
    myTask();
    logDuration.stopAndLog("Heavy calculation task"); // see console log output

    // you can also do all above tasks manually
    Stopwatch notStartedAutomatically = new Stopwatch(false); // creates the stopwatch in stopped state -> has to be started manually
    Duration notStartedDuration = notStartedAutomatically.getElapsed(); // returns a duration of 0 as stopwatch has not been started yet

    notStartedAutomatically.start(); // now start the stopwatch manually
    myTask(); // mimic heavy calculation

    notStartedAutomatically.stop(); // stops the stopwatch manually
    Duration durationAfterStopping = notStartedAutomatically.getElapsed(); // gets the elapsed time in java.time.Duration
    Long durationInNanoseconds = notStartedAutomatically.getElapsedNanos(); // gets the elapsed time in nanoseconds
    Long durationMillis = notStartedAutomatically.getElapsed(TimeUnit.MILLISECONDS); // gets the elapsed time in a desired time unit, milliseconds in this case
```

Also see file [JavaShowcase](src/test/java/net/codinux/util/showcase/JavaShowcase.java).


### Kotlin

Simple usage with wrappers around Stopwatch class:
```kotlin
    // returns the elapsed time
    val measuredDuration = Stopwatch.measureDuration { myTask() }

    // returns elapsed time formatted
    val formattedDuration = Stopwatch.formatDuration { myTask() }

    // logs elapsed time to class' slf4j logger in format: "<action> <formatted_duration>"
    Stopwatch.logDuration("My important task") { myTask() } // see console log output

    // of course you can also specify the (slf4j) logger to log to
    Stopwatch.logDuration("Other task", LoggerFactory.getLogger("Task logger")) { myTask() }

    // logs elapsed time and returns task's result
    val heavyCalculationResult = Stopwatch.logDuration("Task that returns a result") {
        myTask() // mimic heavy calculation
        123L // return result to caller
    }
```

Also see file [KotlinShowcase](src/test/kotlin/net/codinux/util/showcase/KotlinShowcase.kt).


# License

    Copyright 2021 codinux GmbH & Co. KG

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.