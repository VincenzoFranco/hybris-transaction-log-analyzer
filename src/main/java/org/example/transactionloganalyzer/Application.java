package org.example.transactionloganalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.example.transactionloganalyzer.analyzer.TransactionLogAnalyzer;
import org.example.transactionloganalyzer.iohandler.FileInputConsoleOutputHandler;
import org.example.transactionloganalyzer.logs.TransactionLog;
import org.example.transactionloganalyzer.results.TransactionLogResult;

public class Application {

    private final TransactionLogAnalyzer analyzer;
    private final FileInputConsoleOutputHandler ioHandler;

    public Application(FileInputConsoleOutputHandler ioHandler, TransactionLogAnalyzer analyzer) {
        this.ioHandler = ioHandler;
        this.analyzer = analyzer;
    }

    void analyzeFile() throws IOException {
        Map<String, List<TransactionLog>> logMap = new LinkedHashMap<>();

        for (TransactionLog current; (current = ioHandler.getInput()) != null; ) {
            if (!logMap.containsKey(current.getKey())) {
                logMap.put(current.getKey(), new ArrayList<TransactionLog>());
            }
            logMap.get(current.getKey()).add(current);
        }

        Collection<TransactionLogResult> results = analyzer.analyze(logMap);

        for (TransactionLogResult result : results) {
            ioHandler.doOutput(result);
        }
    }
}
