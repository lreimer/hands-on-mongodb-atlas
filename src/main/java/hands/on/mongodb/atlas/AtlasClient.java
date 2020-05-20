package hands.on.mongodb.atlas;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Indexes;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.set;

import org.bson.Document;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "atlas-client", mixinStandardHelpOptions = true, version = "v1.0.0", description = "Test client for MongoDB Atlas")
public class AtlasClient implements Callable<Integer>, AutoCloseable {

    @Option(names = { "--host" }, description = "The database host", defaultValue = "freemongocloud-pjosr.mongodb.net")
    String host;
    @Option(names = { "-d", "--database" }, description = "The database name", defaultValue = "sample_app")
    String database;
    @Option(names = { "-u", "--username" }, description = "The database user", defaultValue = "mongo-user")
    String username;
    @Option(names = { "-p", "--password" }, description = "The database password", arity = "0..1", interactive = true)
    String password;

    private MongoClient mongoClient;
    private MongoDatabase mongoDb;
    private MongoCollection<Document> collection;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new AtlasClient()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        connect();
        listDatabases();
        refreshCollection();
        deleteAllFromCollection();
        insertTestDocuments();
        queryDocuments();
        updateManyDocuments();
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

    private void listDatabases() {
        System.out.println("MongoDB database list:");

        MongoIterable<String> names = mongoClient.listDatabaseNames();
        names.forEach((Consumer<? super String>) System.out::println);

        System.out.println();
    }

    private void refreshCollection() {
        collection = mongoDb.getCollection("documents");
        collection.createIndex(Indexes.ascending("name"));
    }

    private void insertTestDocuments() {
        System.out.println("Insert 50 test documents ...");
        IntStream.range(0, 50).forEachOrdered(i -> {
            Document document = new Document()
                    .append("name", "Document " + i)
                    .append("index", i)
                    .append("next3", Arrays.asList(i + 1, i + 2, i + 3))
                    .append("nested", new Document("name", "Nested " + i));
            System.out.println("Insert " + document);
            collection.insertOne(document);
        });
    }

    private void queryDocuments() {
        System.out.println("Query for all documents ...");
        collection.find().forEach((Consumer<? super Document>) System.out::println);

        System.out.println("Query for first document ...");
        System.out.println(collection.find().first());

        System.out.println("Query the last 25 documents ...");
        collection.find(gt("index", 25)).forEach((Consumer<? super Document>) System.out::println);

        System.out.println("Query by name ...");
        collection.find(eq("name", "Document 49")).forEach((Consumer<? super Document>) System.out::println);
    }

    private void updateManyDocuments() {
        collection.updateMany(lt("index", 25), set("updated", true));
        collection.updateMany(gte("index", 25), set("next3", Arrays.asList(-1)));

        System.out.println("All documents after update ...");
        collection.find().forEach((Consumer<? super Document>) System.out::println);
    }

    private void deleteAllFromCollection() {
        // delete all using empty document
        collection.deleteMany(new Document());
    }
}
