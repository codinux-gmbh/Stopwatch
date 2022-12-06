# Stopwatch
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.codinux.util/stopwatch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.codinux.util/stopwatch)

A simple stopwatch to easily measure and log durations, that's especially easy to use in Kotlin.

## Setup

### Gradle

```
implementation 'net.codinux.util:stopwatch:1.0.3'
```

### Maven

```
<dependency>
    <groupId>net.codinux.util</groupId>
    <artifactId>stopwatch</artifactId>
    <version>1.0.3</version>
</dependency>
```


## Usage


### Kotlin

Simple usage with wrappers around Stopwatch class:
```kotlin
        // returns the elapsed time as Duration instance
        val measuredDuration = Stopwatch.measureDuration { myTask() }
        
        // returns elapsed time as formatted string
        val formattedDuration = Stopwatch.formatDuration { myTask() }
        
        // logs elapsed time to class' slf4j logger in format: "<task> took <formatted_duration>"
        Stopwatch.logDuration("My important task") { myTask() } // see console log output
        
        // of course you can also specify the (slf4j) logger to log to
        Stopwatch.logDuration("Other task", printer = Slf4jMessagePrinter("Task logger")) { myTask() }
        
        // logs elapsed time and returns task's result
        val heavyCalculationResult = Stopwatch.logDuration("Task that returns a result") {
          myTask() // mimic heavy calculation
          "Task result" // return result to caller, heavyCalculationResult will then be `"Task result"`
        }
```

It is also capable to print elapsed time statistics like min, max and average time to a task:
```kotlin
        // adds elapsed time to task's statistics and prints it right away
        (1..3).forEach {
          Stopwatch.logDuration("My important task", addToStatistics = true, printStatisticsNow = true) { myTask() }
        }
        // logs something like this:
        // [main] INFO net.codinux.util.Stopwatch - My important task took 500.179 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task [1]: min 500.179 ms, avg 500.179 ms, max 500.179 ms, total 500.179 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task took 500.161 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task [2]: min 500.161 ms, avg 500.170 ms, max 500.179 ms, total 01.000 s
        // [main] INFO net.codinux.util.Stopwatch - My important task took 500.116 ms
        // [main] INFO net.codinux.util.Stopwatch - My important task [3]: min 500.116 ms, avg 500.152 ms, max 500.179 ms, total 01.500 s
        
        // you can also log the statistics at a defined time:
        (1..10).forEach {
          // only adds the elapsed times to statistics, but doesn't print it
          Stopwatch.addDurationToStatistics("My heavy task") { myTask() }
        }
        Stopwatch.printStatistics("My heavy task") // now print statistics for this task at any time you like. Logs something like this:
        // [main] INFO net.codinux.util.Stopwatch - My heavy task [10]: min 500.126 ms, avg 500.182 ms, max 500.319 ms, total 05.001 s
        
        // statistics by default are logged when the JVM shuts down. This looks something like this:
        // [Shutdown Hook] INFO net.codinux.util.Stopwatch - My heavy task [10]: min 500.126 ms, avg 500.182 ms, max 500.319 ms, total 05.001 s
```

Also see file [KotlinShowcase](src/test/kotlin/net/codinux/util/showcase/KotlinShowcase.kt).

### Java

Static wrapper around Stopwatch:
```java
    // returns the elapsed time
    Duration measuredDuration = Stopwatch.measureDuration(() -> myTask());

    // returns elapsed time formatted
    String formattedDuration = Stopwatch.formatDuration(() -> myTask());

    // logs elapsed time to class' slf4j logger in format: "<task> <formatted_duration>"
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

    // logs elapsed time to class' slf4j logger in format: "<task> <formatted_duration>"
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