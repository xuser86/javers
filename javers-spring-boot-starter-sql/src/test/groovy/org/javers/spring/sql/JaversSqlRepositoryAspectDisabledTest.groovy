package org.javers.spring.sql

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.sql.DummyEntity
import org.javers.spring.boot.sql.DummyEntityRepository
import org.javers.spring.boot.sql.TestApplication
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * @author mwesolowski
 */
@RunWith(SpringJUnit4ClassRunner)
@SpringBootTest(classes = [TestApplication], properties = ["javers.springDataAuditableRepositoryAspectEnabled=false"])
@ActiveProfiles("test")
public class JaversSqlRepositoryAspectDisabledTest {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    @Test
    void "should build javers instance without auto-audit aspect"() {
        //given
        def dummyEntity = new DummyEntity(1, "kaz")

        //when
        dummyEntityRepository.save(dummyEntity)

        //then
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(DummyEntity).build())
        assert snapshots.size() == 0
    }
}