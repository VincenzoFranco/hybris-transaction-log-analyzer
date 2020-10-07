package org.example.transactionloganalyzer.logs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionLogFactory {

    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    public TransactionLog createLog(String line) {
        return createLogInternal(line);
    }

    private TransactionLog createLogInternal(String line) {
        line = replaceEmptyIp(line);
        String thread = retrieveThreadName(line).trim();
        line = line.substring(thread.length() + 1);

        try {
            String[] splitted = line.split(" ");
            Date date = DATE_PARSER.parse(splitted[0] + " " + splitted[1]);
            String ip = splitted[3];
            TransactionLogType type = TransactionLogType.fromValue(splitted[7]);

            String objectId;
            int count;
            if (TransactionLogType.EXECUTE != type) {
                String[] objectIdAndCount = splitted[9].split(",");
                objectId = objectIdAndCount[0].split("=")[1];
                count = Integer.parseInt(objectIdAndCount[1].split("=")[1]);
            } else {
                String[] objectIdSplit = splitted[9].split("#");
                objectId = objectIdSplit[1];
                count = 0;
            }

            String key = String.format("%s_%s", thread, objectId);
            return new TransactionLog(key, thread, date, ip, type, objectId, count);
        } catch (RuntimeException e) {
            System.out.println("Got exception in parsing: " + line);
            throw e;
        } catch (ParseException e) {
            System.out.println("Got exception in parsing: " + line);
            throw new RuntimeException(e);
        }
    }

    private String retrieveThreadName(String line) {
        String[] threadSplit = line.split("\\d{4}-\\d{2}-\\d{2}");
        return line.substring(0, threadSplit[0].length());
    }

    private String replaceEmptyIp(String line) {
        return line.replaceAll("] ]", "]]");
    }
}
