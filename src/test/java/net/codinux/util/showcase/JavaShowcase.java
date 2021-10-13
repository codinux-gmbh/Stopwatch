package net.codinux.util.showcase;

import net.codinux.util.Stopwatch;
import org.slf4j.LoggerFactory;

import java.time.Duration;
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
    }

    private void showInstanceMethods() {
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
    }

}