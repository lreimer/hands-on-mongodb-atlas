package hands.on.mongodb.atlas

import spock.lang.Specification

class AtlasClientSpec extends Specification {
    def "AtlasClient can connect"() {
        given:
        def client = new AtlasClient()

        and:
        client.host = "localhost";
        client.database = "test";
        client.username = "mongo-user";
        client.password = "secret"

        when:
        def connectionString = client.connectionString

        then:
        connectionString == "mongodb+srv://mongo-user:secret@localhost/test?retryWrites=true&w=majority"
    }
}
