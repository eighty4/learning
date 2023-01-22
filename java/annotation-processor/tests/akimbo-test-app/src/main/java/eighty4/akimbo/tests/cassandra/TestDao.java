package eighty4.akimbo.tests.cassandra;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

import java.util.UUID;

@Dao
public interface TestDao {

    @Select
    TestEntity selectRecordById(UUID uuid);

    @Insert
    void save(TestEntity user);

}
