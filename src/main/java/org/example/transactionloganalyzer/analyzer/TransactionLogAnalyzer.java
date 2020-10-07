package org.example.transactionloganalyzer.analyzer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.example.transactionloganalyzer.logs.TransactionLog;
import org.example.transactionloganalyzer.logs.TransactionLogType;
import org.example.transactionloganalyzer.results.BigTransactionResult;
import org.example.transactionloganalyzer.results.EmptyGroupResult;
import org.example.transactionloganalyzer.results.MultipleIPResult;
import org.example.transactionloganalyzer.results.TransactionLogResult;
import org.example.transactionloganalyzer.results.WrongCountResult;

public class TransactionLogAnalyzer {

    public List<TransactionLogResult> analyze(Map<String, List<TransactionLog>> logGroups) {
        List<TransactionLogResult> results = new ArrayList<>();

        for (List<TransactionLog> logGroup : logGroups.values()) {
            if (logGroup.isEmpty()) {
                results.add(new EmptyGroupResult("Log group empty"));
            } else if (!beginWithZero(logGroup) || !endWithOne(logGroup)) {
                results.add(new WrongCountResult("Log starting/ending with wrong count, " + logGroup.get(0)));
            } else if (!sameIp(logGroup)) {
                results.add(new MultipleIPResult("Multiple IP in log group: " + logGroup.get(0)));
            } else {
                Date firstDate = logGroup.get(0).getDate();
                Date lastDate = logGroup.get(logGroup.size() - 1).getDate();
                long diffInMillies = Math.abs(lastDate.getTime() - firstDate.getTime());
                long diff = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if (diff >= 10) {
                    String message = String.format("Big group found and time %s seconds with %s internal transactions, can cause issues: %s", diff, logGroup.size(), logGroup.get(0));
                    results.add(new BigTransactionResult(message));
                }
            }
        }

        return results;
    }

    private boolean sameIp(List<TransactionLog> logGroup) {
        String firstIp = logGroup.get(0).getIp();
        for (TransactionLog log : logGroup) {
            if (!(firstIp.equals(log.getIp()))) {
                return false;
            }
        }
        return true;
    }

    private boolean endWithOne(List<TransactionLog> logGroup) {
        TransactionLog lastLog = logGroup.get(logGroup.size() - 1);
        return 1 == lastLog.getCount() && (TransactionLogType.COMMIT == lastLog.getType() || TransactionLogType.ROLLBACK == lastLog.getType());
    }

    private boolean beginWithZero(List<TransactionLog> logGroup) {
        if (TransactionLogType.EXECUTE == logGroup.get(0).getType()) {
            if(logGroup.size() > 1) {
                return TransactionLogType.BEGIN == logGroup.get(1).getType() && logGroup.get(1).getCount() == 0;
            } else {
                return logGroup.get(0).getCount() == 0;
            }
        } else if (TransactionLogType.BEGIN == logGroup.get(0).getType()) {
            return logGroup.get(0).getCount() == 0;
        }
        return false;
    }
}
