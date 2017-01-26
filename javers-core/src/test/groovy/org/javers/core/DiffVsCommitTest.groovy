package org.javers.core

import org.javers.core.diff.Change
import org.javers.core.diff.Diff
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import javax.persistence.Id

class Container1VO implements Serializable {
    @Id
    public Long id;
    public String name;
    public List<Container2VO> list;

    Container1VO(Long id, String name) {
        this.id = id;
        this.name = name;
        this.list = new ArrayList<Container2VO>();
    }
}

class Container2VO implements Serializable {
    @Id
    public Long id;

    public String name;

    public List<Element1VO> list;

    Container2VO(Long id, String name) {
        this.id = id;
        this.name = name;
        this.list = new ArrayList<Element1VO>();
    }
}

class Element1VO implements Serializable {
    @Id
    public Long id;
    public String name;
    public String secondName;

    Element1VO(Long id, String name, String secondName) {
        this.id = id;
        this.name = name;
        this.secondName = secondName;
    }
}

/**
 * Created by rafal on 26.01.17.
 */
class DiffVsCommitTest extends Specification {
    def "commitVsDiff"() {

        Javers javers = JaversBuilder.javers().build();

        when:
        Container1VO obj1 = new Container1VO(4L, "blok2")
        obj1.list.add(new Container2VO(400L, "sekcja1"))
        obj1.list.get(0).list.add(new Element1VO(400L, "pozycjaKosztowa1", "pozycja#1"))

        Container1VO obj2 = new Container1VO(4L, "blok2");

        Diff diff = javers.compare(obj1, obj2)
        println(diff)

        javers.commit("test1", obj1)
        javers.commit("test1", obj2)

        List<Change> changes = javers.findChanges( QueryBuilder.byInstanceId(4L, Container1VO.class).build() )
        println(changes)

        then:
        diff.changes.size() == changes.size()
    }
}
