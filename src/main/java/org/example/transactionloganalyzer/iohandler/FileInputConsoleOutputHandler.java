package org.example.transactionloganalyzer.iohandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import org.example.transactionloganalyzer.logs.TransactionLogFactory;
import org.example.transactionloganalyzer.logs.TransactionLog;
import org.example.transactionloganalyzer.results.TransactionLogResult;

public class FileInputConsoleOutputHandler {

    private final TransactionLogFactory logFactory;
    private final BufferedReader reader;
    private final PrintStream writer;

    public FileInputConsoleOutputHandler(BufferedReader reader, PrintStream writer, TransactionLogFactory logFactory) {
        this.reader = reader;
        this.writer = writer;
        this.logFactory = logFactory;
    }

    public TransactionLog getInput() throws IOException {
        String line = reader.readLine();
        if(line != null) {
            return logFactory.createLog(line);
        }
        return null;
    }

    public void doOutput(TransactionLogResult result) {
        writer.println(result);
    }
}
