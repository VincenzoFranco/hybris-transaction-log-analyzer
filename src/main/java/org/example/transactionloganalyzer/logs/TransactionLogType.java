package org.example.transactionloganalyzer.logs;

public enum TransactionLogType {
    COMMIT("tx.commit"),
    ROLLBACK("tx.rollback"),
    EXECUTE("tx.execute(body)"),
    BEGIN("tx.begin");

    private final String pattern;

    TransactionLogType(String pattern) {
        this.pattern = pattern;
    }

    public static TransactionLogType fromValue(String pattern) {
        for (TransactionLogType type : TransactionLogType.values()) {
            if (pattern.equals(type.pattern)) {
                return type;
            }
        }
        return null;
    }
}
