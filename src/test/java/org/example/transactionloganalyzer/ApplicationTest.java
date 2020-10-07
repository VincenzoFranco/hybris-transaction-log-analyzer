package org.example.transactionloganalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.example.transactionloganalyzer.analyzer.TransactionLogAnalyzer;
import org.example.transactionloganalyzer.iohandler.FileInputConsoleOutputHandler;
import org.example.transactionloganalyzer.logs.TransactionLog;
import org.example.transactionloganalyzer.results.TransactionLogResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;

class ApplicationTest {

    private Application application;
    private final FileInputConsoleOutputHandler ioHandler = Mockito.mock(FileInputConsoleOutputHandler.class);
    private final TransactionLogAnalyzer analyzer = Mockito.mock(TransactionLogAnalyzer.class);

    @BeforeEach
    public void setUp() {
        application = new Application(ioHandler, analyzer);
    }

    @Test
    void whenThereIsNoInputThenNoOutputIsExpected() throws IOException {
        TransactionLog testLog = Mockito.mock(TransactionLog.class);
        Mockito.when(ioHandler.getInput()).thenReturn(null);
        Mockito.when(analyzer.analyze(any(Map.class))).thenReturn(new ArrayList<TransactionLogResult>());

        application.analyzeFile();

        Mockito.verify(ioHandler, Mockito.times(1)).getInput();
        Mockito.verifyNoMoreInteractions(ioHandler);
    }

    @Test
    void analyzeShouldReadFromInputAndSendOuput() throws IOException {
        TransactionLog testLog = Mockito.mock(TransactionLog.class);
        TransactionLogResult testResult = Mockito.mock(TransactionLogResult.class);
        Mockito.when(ioHandler.getInput()).thenReturn(testLog).thenReturn(null);
        Mockito.when(analyzer.analyze(any(Map.class))).thenReturn(Collections.singletonList(testResult));

        application.analyzeFile();

        Mockito.verify(ioHandler, Mockito.times(2)).getInput();
        Mockito.verify(ioHandler).doOutput(testResult);
    }

    @Test
    void analyzeShouldReadFromInputAndSendOuputAlsoWithEmptyResults() throws IOException {
        TransactionLog testLog = Mockito.mock(TransactionLog.class);
        Mockito.when(ioHandler.getInput()).thenReturn(testLog).thenReturn(null);
        Mockito.when(analyzer.analyze(any(Map.class))).thenReturn(new ArrayList<TransactionLogResult>());

        application.analyzeFile();

        Mockito.verify(ioHandler, Mockito.times(2)).getInput();
        Mockito.verifyNoMoreInteractions(ioHandler);
    }
}