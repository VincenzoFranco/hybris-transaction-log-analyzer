package org.example.transactionloganalyzer.iohandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import org.example.transactionloganalyzer.logs.TransactionLog;
import org.example.transactionloganalyzer.logs.TransactionLogFactory;
import org.example.transactionloganalyzer.results.EmptyGroupResult;
import org.example.transactionloganalyzer.results.TransactionLogResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

class FileInputConsoleOutputHandlerTest {

    FileInputConsoleOutputHandler fileInputConsoleOutputHandler;
    BufferedReader reader = Mockito.mock(BufferedReader.class);
    TransactionLogFactory logFactory = Mockito.mock(TransactionLogFactory.class);
    PrintStream printStream = Mockito.mock(PrintStream.class);
    TransactionLog log = Mockito.mock(TransactionLog.class);

    @BeforeEach
    public void setUp() {
        fileInputConsoleOutputHandler = new FileInputConsoleOutputHandler(reader, printStream, logFactory);
    }

    @Test
    public void inputShouldComeFromReader() throws IOException {
        Mockito.when(reader.readLine()).thenReturn("Test");
        Mockito.when(logFactory.createLog("Test")).thenReturn(log);

        try {
            TransactionLog input = fileInputConsoleOutputHandler.getInput();
            assertSame(log, input);
        } catch (IOException e) {
            fail("Exception not expected");
        }

        Mockito.verify(reader, Mockito.only()).readLine();
        Mockito.verify(logFactory, Mockito.only()).createLog("Test");
    }

    @Test
    public void inputExceptionShouldBeRethrown() throws IOException {
        Mockito.when(reader.readLine()).thenThrow(IOException.class);

        try {
            fileInputConsoleOutputHandler.getInput();
            fail("Exception expected");
        } catch (IOException ignored) {
        }
    }

    @Test
    public void outputShouldBeRedirectedToPrintWriter() {
        TransactionLogResult result = new EmptyGroupResult("Test");
        fileInputConsoleOutputHandler.doOutput(result);
        Mockito.verify(printStream, Mockito.only()).println(result);
    }
}