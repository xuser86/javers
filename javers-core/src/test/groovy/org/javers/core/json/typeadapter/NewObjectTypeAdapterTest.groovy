package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.core.json.builder.ChangeTestBuilder.newObject

/**
 * @author bartosz walacik
 */
class NewObjectTypeAdapterTest extends Specification {
    def "should serialize NewObject"() {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def change = newObject(new DummyUser(name:"kaz"))

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == "NewObject"
        json.globalCdoId.entity == "org.javers.core.model.DummyUser"
        json.globalCdoId.cdoId == "kaz"
    }

}