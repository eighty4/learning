package eighty4.akimbo.tests.cassandra;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import eighty4.akimbo.annotation.http.HttpService;
import eighty4.akimbo.annotation.http.RequestMapping;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.UUID;

@HttpService
public class CassandraScenarios {

    private final TestDao testDao;
    private UUID testRecordId;

    @Inject
    public CassandraScenarios(TestDao testDao) {
        this.testDao = testDao;
    }

    @PostConstruct
    public void saveTestRecord() {
        TestEntity testRecord = new TestEntity();
        testRecordId = Uuids.timeBased();
        testRecord.setId(testRecordId);
        testRecord.setColumn("column data");
        testDao.save(testRecord);
    }

    @RequestMapping("/cassandra/dao/component")
    public TestEntity cassandraDaoTest() {
        return testDao.selectRecordById(testRecordId);
    }
}
