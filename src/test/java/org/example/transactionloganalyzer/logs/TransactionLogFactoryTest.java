package org.example.transactionloganalyzer.logs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class TransactionLogFactoryTest {

    private static final String JOB_BEGIN = "Order.DeleteDeadConsignmentsCronJob_NEW::de.hybris.platform.servicelayer.internal.jalo.ServicelayerJob 2020-09-30 10:21:44,803 DEBUG [] () [de.hybris.platform.tx.Transaction] entering tx.begin of objectID=55785854,opentxcount=0";
    private static final String JOB_EXECUTE = "Everything Before Date 2020-09-30 10:21:44,803 DEBUG [] () [de.hybris.platform.tx.Transaction] entering tx.execute(body) of objectID#55785854";
    private static final String JOB_COMMIT = "Thread 2020-09-30 10:21:44,797 DEBUG [0.1.2.3] () [de.hybris.platform.tx.Transaction] entering tx.commit of objectID=55785854,opentxcount=1";
    private static final String JOB_ROLLBACK = "Thread 2020-09-30 10:21:44,797 DEBUG [EverythingInsideSquareWithoutSpace] () [de.hybris.platform.tx.Transaction] entering tx.rollback of objectID=55785854,opentxcount=1";

    private TransactionLogFactory transactionLogFactory;

    @BeforeEach
    public void setUp() {
        transactionLogFactory = new TransactionLogFactory();
    }

    @Test
    public void transactionLogFromJobBegin() throws ParseException {
        TransactionLog log = transactionLogFactory.createLog(JOB_BEGIN);
        assertNotNull("Log should not be null", log);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date expected = sdf.parse("2020-09-30 10:21:44,803");
        assertEquals(0, log.getDate().compareTo(expected));
        assertEquals(0, log.getCount());
        assertEquals("[]", log.getIp());
        assertEquals(TransactionLogType.BEGIN, log.getType());
        assertEquals("55785854", log.getObjectId());
        assertEquals("Order.DeleteDeadConsignmentsCronJob_NEW::de.hybris.platform.servicelayer.internal.jalo.ServicelayerJob_55785854", log.getKey());
    }

    @Test
    public void transactionLogFromJobExecute() throws ParseException {
        TransactionLog log = transactionLogFactory.createLog(JOB_EXECUTE);
        assertNotNull("Log should not be null", log);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date expected = sdf.parse("2020-09-30 10:21:44,803");
        assertEquals(0, log.getDate().compareTo(expected));
        assertEquals(0, log.getCount());
        assertEquals("[]", log.getIp());
        assertEquals(TransactionLogType.EXECUTE, log.getType());
        assertEquals("55785854", log.getObjectId());
        assertEquals("Everything Before Date_55785854", log.getKey());
    }

    @Test
    public void transactionLogFromJobCommit() throws ParseException {
        TransactionLog log = transactionLogFactory.createLog(JOB_COMMIT);
        assertNotNull("Log should not be null", log);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date expected = sdf.parse("2020-09-30 10:21:44,797");
        assertEquals(0, log.getDate().compareTo(expected));
        assertEquals(1, log.getCount());
        assertEquals("[0.1.2.3]", log.getIp());
        assertEquals(TransactionLogType.COMMIT, log.getType());
        assertEquals("55785854", log.getObjectId());
        assertEquals("Thread_55785854", log.getKey());
    }

    @Test
    public void transactionLogFromJobRollback() throws ParseException {
        TransactionLog log = transactionLogFactory.createLog(JOB_ROLLBACK);
        assertNotNull("Log should not be null", log);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date expected = sdf.parse("2020-09-30 10:21:44,797");
        assertEquals(0, log.getDate().compareTo(expected));
        assertEquals(1, log.getCount());
        assertEquals("[EverythingInsideSquareWithoutSpace]", log.getIp());
        assertEquals(TransactionLogType.ROLLBACK, log.getType());
        assertEquals("55785854", log.getObjectId());
        assertEquals("Thread_55785854", log.getKey());
    }
}