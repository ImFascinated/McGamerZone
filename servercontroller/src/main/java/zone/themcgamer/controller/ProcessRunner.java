package zone.themcgamer.controller;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Braydon
 */
public class ProcessRunner extends Thread {
    private final ProcessBuilder builder;
    private Process process;
    private Consumer<Boolean> callback;

    @Getter private boolean done;
    private boolean error;

    public ProcessRunner(String[] args) {
        super("ProcessRunner - " + Arrays.toString(args));
        builder = new ProcessBuilder(args);
    }

    @Override
    public void run() {
        try {
            process = builder.start();
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                if (line.equals("255"))
                    error = true;
                line = reader.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            done = true;
            if (callback != null)
                callback.accept(error);
        }
    }

    public void start(Consumer<Boolean> callback) {
        super.start();
        this.callback = callback;
    }

    public int exitValue() {
        if (process == null)
            throw new IllegalStateException("Process was not started!");
        return process.exitValue();
    }

    public void abort() {
        if (!isDone())
            process.destroy();
    }
}