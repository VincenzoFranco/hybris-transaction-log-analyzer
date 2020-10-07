package org.example.transactionloganalyzer.logs;

import java.util.Date;

public class TransactionLog {

    private final String key;
    private final String thread;
    private final Date date;
    private final String ip;
    private final TransactionLogType type;
    private final String objectId;
    private final int count;

    public TransactionLog(String key, String thread, Date date, String ip, TransactionLogType type, String objectId, int count) {
        this.key = key;
        this.thread = thread;
        this.date = date;
        this.ip = ip;
        this.type = type;
        this.objectId = objectId;
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public String getIp() {
        return ip;
    }

    public TransactionLogType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public String getObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return "TransactionLog{" +
                "thread='" + thread + '\'' +
                ", date='" + date + '\'' +
                ", ip='" + ip + '\'' +
                ", type=" + type +
                ", objectId='" + objectId + '\'' +
                ", count=" + count +
                '}';
    }

    public String getKey() {
        return key;
    }

}
