# Stopwatch
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.codinux.util/stopwatch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.codinux.util/stopwatch)

Zero dependency Stopwatch with a lot of convenience functions to easily measure and log durations and log statistics of measured durations.

## Setup

### Gradle

```
implementation("net.codinux.util:stopwatch:1.5.0")
```

### Maven

```
<dependency>
    <groupId>net.codinux.util</groupId>
    <artifactId>stopwatch</artifactId>
    <version>1.5.0</version>
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
        Stopwatch.DefaultLogger = Slf4jLogger("Task logger")
        Stopwatch.logDuration("Other task") { myTask() }
        
        // logs elapsed time and returns task's result
        val heavyCalculationResult = Stopwatch.logDuration("Task that returns a result") {
          myTask() // mimic heavy calculation
          "Task result" // return result to caller, heavyCalculationResult will then be `"Task result"`
        }
```

Log statistics of a task's measured durations like min, max and average time:
```kotlin
        // adds elapsed time to task's statistics and logs it right away
        (1..3).forEach {
          Stopwatch.logDuration("My important task", addToStatistics = true, logStatisticsNow = true) { myTask() }
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
          // only adds the elapsed times to statistics, but doesn't log it
          Stopwatch.addDurationToStatistics("My heavy task") { myTask() }
        }
        Stopwatch.logStatistics("My heavy task") // now log statistics for this task at any time you like. Logs something like this:
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

## Logging

Calls like `logDuration()` automatically log the elapsed time to an log appender configured with [KMP-Log](https://github.com/codinux-gmbh/KMP-Log).  
Therefor it has all the platform specific log appenders that KMP-Log has, e.g. slf4j on JVM, Logcat on Android, OSLog on Apple systems, JavaScript Console in Browser / Node.js, Console on other native platforms, ...  
How to configure logging with KMP-Log see on the [project website](https://github.com/codinux-gmbh/KMP-Log).

Or implement your own logging:
```kotlin
class CustomMessageLogger : MessageLogger {

    override fun info(message: String) {
        // implement your custom logging here
        System.err.println(message)
    }

}

class CustomLoggerShowcase {

    fun runShowcase() {
        // set default message logger for all instances:
        Stopwatch.DefaultLogger = CustomMessageLogger()

        // now CustomMessageLogger gets used:
        Stopwatch.logDuration("Heavy calculation") { heavyCalculation() }

        // or set the custom message logger only for a specific Stopwatch instance
        val stopwatch = Stopwatch(logger = CustomMessageLogger())
        heavyCalculation()
        stopwatch.logElapsedTime("Heavy calculation")
    }

    private fun heavyCalculation() {
        // mimic heavy calculation
        TimeUnit.MILLISECONDS.sleep(300)
    }
}
```

For the code see file [CustomLoggerShowcase](src/test/kotlin/net/codinux/util/showcase/CustomLoggerShowcase.java).


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