package hands.on.mongodb.atlas;

import java.util.concurrent.Callable;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "atlas-client", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Test client for MongoDB Atlas")
public class AtlasClient implements Callable<Integer>, AutoCloseable {

    @Option(names = { "--host" }, description = "The database host", fallbackValue = "freemongocloud-pjosr.mongodb.net")
    String host;
    @Option(names = { "-d", "--database" }, description = "The database name", fallbackValue = "test")
    String database;
    @Option(names = { "-u", "--username" }, description = "The database user", fallbackValue = "mongo-user")
    String username;
    @Option(names = { "-p", "--password" }, description = "The database password", interactive = true)
    String password;

    private MongoClient mongoClient;
    private MongoDatabase mongoDb;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new AtlasClient()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        connect();

        close();
        return 0;
    }

    void connect() {
        mongoClient = MongoClients.create(getConnectionString());
        mongoDb = mongoClient.getDatabase(database);
    }

    String getConnectionString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("mongodb+srv://")
                .append(username).append(":").append(password)
                .append("@").append(host)
                .append("/").append(database)
                .append("?retryWrites=true&w=majority").toString();
    }

    @Override
    public void close() throws Exception {
        mongoClient.close();
    }
}
