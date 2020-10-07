package org.example.transactionloganalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.example.transactionloganalyzer.analyzer.TransactionLogAnalyzer;
import org.example.transactionloganalyzer.iohandler.FileInputConsoleOutputHandler;
import org.example.transactionloganalyzer.logs.TransactionLogFactory;

public class Main {

    public static void main(String[] args) {
        checkArgsOrExit(args);
        File file = checkFileOrExit(args);

        try (
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(streamReader)
        ) {
            TransactionLogFactory logFactory = new TransactionLogFactory();
            TransactionLogAnalyzer analyzer = new TransactionLogAnalyzer();

            FileInputConsoleOutputHandler ioHandler = new FileInputConsoleOutputHandler(reader, System.out, logFactory);
            Application application = new Application(ioHandler, analyzer);
            System.out.println("Start analysis");
            application.analyzeFile();
            System.out.println("End analysis");
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void checkArgsOrExit(String[] args) {
        if (args.length < 1) {
            System.out.println("You have to specify the file");
            System.exit(1);
        }
    }

    private static File checkFileOrExit(String[] args) {
        File file = new File(args[0]);
        if (!file.exists()) {
            System.out.println("File not found " + args[0]);
            System.exit(1);
        }
        return file;
    }
}
