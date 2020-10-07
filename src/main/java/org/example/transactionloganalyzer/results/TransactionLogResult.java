package org.example.transactionloganalyzer.results;

public abstract class TransactionLogResult {

    private final String message;

    public TransactionLogResult(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
