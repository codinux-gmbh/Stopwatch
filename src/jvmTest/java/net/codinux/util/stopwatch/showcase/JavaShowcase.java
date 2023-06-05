package net.codinux.util.stopwatch.showcase;

import net.codinux.util.stopwatch.Duration;
import net.codinux.util.stopwatch.Stopwatch;
import net.codinux.util.stopwatch.StopwatchJava;
import net.codinux.util.stopwatch.output.KmpLogMessageLogger;

import java.util.concurrent.TimeUnit;

/**
 * Shows the usage of {@link Stopwatch} in Java.
 */
public class JavaShowcase {

    public static void main(String[] args) {
        new JavaShowcase().runShowcase();
    }

    private static void myTask() {
        wait(123, TimeUnit.MILLISECONDS);
    }

    private static long myTaskWithResult() {
        myTask();
        return 123L;
    }

    private static void wait(long time, TimeUnit unit) {
        try {
            unit.sleep(time);
        } catch (Exception ignored) { }
    }


    public void runShowcase() {
        showStaticMethods();

        showInstanceMethods();
    }

    private void showStaticMethods() {
        // returns the elapsed time
        Duration measuredDuration = StopwatchJava.measureDuration(() -> myTask());

        // returns elapsed time formatted
        String formattedDuration = StopwatchJava.formatDuration(() -> myTask());

        // logs elapsed time to class' slf4j logger in format: "<task> <formatted_duration>"
        StopwatchJava.logDuration("My important task", () -> myTask()); // see console log output

        // of course you can also customize the (slf4j) logger to log to
        Stopwatch.setDefaultLogger(new KmpLogMessageLogger("Task logger"));
        StopwatchJava.logDuration("Other task", false, false, () -> myTask());

        // logs elapsed time and returns task's result
        long heavyCalculationResult = StopwatchJava.logDuration("Task that returns a result", () -> myTaskWithResult());
    }

    private void showInstanceMethods() {
        // returns the elapsed time
        Stopwatch measureDuration = new Stopwatch(); // will automatically be created in started state
        myTask();
        long measuredDurationNanos = measureDuration.stopNanos();
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
        net.codinux.util.stopwatch.Duration notStartedDuration = notStartedAutomatically.getElapsed(); // returns a duration of 0 as stopwatch has not been started yet

        notStartedAutomatically.start(); // now start the stopwatch manually
        myTask(); // mimic heavy calculation

        notStartedAutomatically.stopNanos(); // stops the stopwatch manually
        net.codinux.util.stopwatch.Duration durationAfterStopping = notStartedAutomatically.getElapsed(); // gets the elapsed time in java.time.Duration
        Long durationInNanoseconds = notStartedAutomatically.getElapsedNanos(); // gets the elapsed time in nanoseconds
        Long durationMillis = notStartedAutomatically.getElapsed(net.codinux.util.stopwatch.DurationUnit.Milliseconds); // gets the elapsed time in a desired time unit, milliseconds in this case
    }

}