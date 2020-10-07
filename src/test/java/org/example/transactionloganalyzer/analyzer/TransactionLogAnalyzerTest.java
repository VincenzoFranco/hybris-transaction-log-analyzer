package org.example.transactionloganalyzer.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.example.transactionloganalyzer.logs.TransactionLog;
import org.example.transactionloganalyzer.logs.TransactionLogType;
import org.example.transactionloganalyzer.results.BigTransactionResult;
import org.example.transactionloganalyzer.results.EmptyGroupResult;
import org.example.transactionloganalyzer.results.MultipleIPResult;
import org.example.transactionloganalyzer.results.TransactionLogResult;
import org.example.transactionloganalyzer.results.WrongCountResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class TransactionLogAnalyzerTest {

    TransactionLogAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        analyzer = new TransactionLogAnalyzer();
    }

    @Test
    public void emptyMapShouldNotReturnErrors() {
        Map<String, List<TransactionLog>> map = new HashMap<>();
        Collection<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(0, results.size());
    }

    @Test
    public void happyCommitFlowShouldNotReturnErrors() {
        List<TransactionLog> logs = new ArrayList<>();
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.EXECUTE, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.COMMIT, "1", 1));
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        Collection<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(0, results.size());
    }

    @Test
    public void happyRollbackFlowShouldNotReturnErrors() {
        List<TransactionLog> logs = new ArrayList<>();
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.EXECUTE, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.ROLLBACK, "1", 1));
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        Collection<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(0, results.size());
    }

    @Test
    public void happyNestedFlowShouldNotReturnErrors() {
        List<TransactionLog> logs = new ArrayList<>();
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.EXECUTE, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 1));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.COMMIT, "1", 2));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.ROLLBACK, "1", 1));
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        Collection<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(0, results.size());
    }

    @Test
    public void happyFlowWithoutExecuteShouldNotReturnErrors() {
        List<TransactionLog> logs = new ArrayList<>();
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.COMMIT, "1", 1));
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        Collection<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(0, results.size());
    }

    @Test
    public void emptyGroupShouldReturnError() {
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", Collections.<TransactionLog>emptyList());
        List<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(1, results.size());
        assertEquals(EmptyGroupResult.class, results.get(0).getClass());
    }

    @Test
    public void multipleIPInLogGroupShouldReturnError() {
        List<TransactionLog> logs = new ArrayList<>();
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.COMMIT, "1", 1));
        logs.add(new TransactionLog("1", "2", new Date(), "222", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "222", TransactionLogType.COMMIT, "1", 1));
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        List<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(1, results.size());
        assertEquals(MultipleIPResult.class, results.get(0).getClass());
    }

    @Test
    public void startingWithWrongCountShouldReturnError() {
        List<TransactionLog> logs = new ArrayList<>();
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 1));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.COMMIT, "1", 2));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.COMMIT, "1", 1));
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        List<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(1, results.size());
        assertEquals(WrongCountResult.class, results.get(0).getClass());
    }

    @Test
    public void endingWithWrongCountShouldReturnError() {
        List<TransactionLog> logs = new ArrayList<>();
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.BEGIN, "1", 1));
        logs.add(new TransactionLog("1", "2", new Date(), "111", TransactionLogType.COMMIT, "1", 2));
        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        List<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(1, results.size());
        assertEquals(WrongCountResult.class, results.get(0).getClass());
    }

    @Test
    public void longTransactionShouldReturnError() {
        List<TransactionLog> logs = new ArrayList<>();
        Date now = new Date();
        Date nowPlus10Seconds = new Date(now.getTime() + (10 * 1000));
        logs.add(new TransactionLog("1", "2", now, "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", now, "111", TransactionLogType.BEGIN, "1", 1));
        logs.add(new TransactionLog("1", "2", now, "111", TransactionLogType.COMMIT, "1", 2));
        logs.add(new TransactionLog("1", "2", nowPlus10Seconds, "111", TransactionLogType.BEGIN, "1", 1));
        logs.add(new TransactionLog("1", "2", nowPlus10Seconds, "111", TransactionLogType.COMMIT, "1", 2));
        logs.add(new TransactionLog("1", "2", nowPlus10Seconds, "111", TransactionLogType.COMMIT, "1", 1));

        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        List<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(1, results.size());
        assertEquals(BigTransactionResult.class, results.get(0).getClass());
    }

    @Test
    public void veryLongTransactionShouldReturnError() {
        List<TransactionLog> logs = new ArrayList<>();
        Date now = new Date();
        Date nowPlus30Seconds = new Date(now.getTime() + (31 * 1000));
        logs.add(new TransactionLog("1", "2", now, "111", TransactionLogType.BEGIN, "1", 0));
        logs.add(new TransactionLog("1", "2", now, "111", TransactionLogType.BEGIN, "1", 1));
        logs.add(new TransactionLog("1", "2", now, "111", TransactionLogType.COMMIT, "1", 2));
        logs.add(new TransactionLog("1", "2", nowPlus30Seconds, "111", TransactionLogType.BEGIN, "1", 1));
        logs.add(new TransactionLog("1", "2", nowPlus30Seconds, "111", TransactionLogType.COMMIT, "1", 2));
        logs.add(new TransactionLog("1", "2", nowPlus30Seconds, "111", TransactionLogType.COMMIT, "1", 1));

        Map<String, List<TransactionLog>> map = new HashMap<>();
        map.put("KEY", logs);
        List<TransactionLogResult> results = analyzer.analyze(map);
        assertEquals(1, results.size());
        assertEquals(BigTransactionResult.class, results.get(0).getClass());
    }

    @Test
    public void shouldBePossibleCreateMultipleErrorsOrWarnings() {
        List<TransactionLog> logs1 = new ArrayList<>();
        List<TransactionLog> logs2 = new ArrayList<>();
        logs1.add(new TransactionLog("L1", "T1", new Date(), "IP1", TransactionLogType.BEGIN, "1", 0));
        logs1.add(new TransactionLog("L1", "T1", new Date(), "IP1", TransactionLogType.BEGIN, "1", 1));
        logs1.add(new TransactionLog("L1", "T1", new Date(), "IP1", TransactionLogType.COMMIT, "1", 2));
        logs2.add(new TransactionLog("L2", "T2", new Date(), "IP1", TransactionLogType.BEGIN, "1", 0));
        logs2.add(new TransactionLog("L2", "T2", new Date(), "IP1", TransactionLogType.COMMIT, "1", 1));
        logs2.add(new TransactionLog("L2", "T2", new Date(), "IP2", TransactionLogType.BEGIN, "1", 0));
        logs2.add(new TransactionLog("L2", "T2", new Date(), "IP2", TransactionLogType.COMMIT, "1", 1));
        Map<String, List<TransactionLog>> map = new LinkedHashMap<>();
        map.put("KEY1", logs1);
        map.put("KEY2", logs2);

        List<TransactionLogResult> results = analyzer.analyze(map);

        assertEquals(2, results.size());
        assertEquals(WrongCountResult.class, results.get(0).getClass());
        assertEquals(MultipleIPResult.class, results.get(1).getClass());
    }

}