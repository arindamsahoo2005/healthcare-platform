package com.healthcare.platform.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Configuration
@EnableMongoRepositories(basePackages = "com.healthcare.platform.repository.mongo")
public class MongoConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    private MongoServer embeddedServer;

    @Value("${spring.data.mongodb.uri:#{null}}")
    private String configuredUri;

    private static String activeConnectionString = "mongodb://localhost:27017/healthcaredb";
    private static int activePort = 27017;
    private static boolean isExternal = false;

    public static String getActiveConnectionString() { return activeConnectionString; }
    public static int getActivePort() { return activePort; }
    public static boolean isExternalServer() { return isExternal; }

    @Bean
    public MongoClient mongoClient() {
        // 1. If explicit URI is provided, use it directly (e.g. MongoDB Atlas or custom server)
        if (configuredUri != null && !configuredUri.isBlank()) {
            activeConnectionString = configuredUri;
            log.info("Connecting to explicitly configured MongoDB at: {}", configuredUri.replaceAll("://.*@", "://***@"));
            return MongoClients.create(configuredUri);
        }

        // 2. Check if a local external MongoDB server is already listening on port 27017
        boolean externalMongoAvailable = isPortInUse("localhost", 27017);
        if (externalMongoAvailable) {
            isExternal = true;
            activePort = 27017;
            activeConnectionString = "mongodb://localhost:27017/healthcaredb";
            log.info("Detected active local MongoDB service on port 27017. Connecting to mongodb://localhost:27017/healthcaredb");
            return MongoClients.create(activeConnectionString);
        }

        // 3. Fallback: Start lightweight in-memory pure-Java wire-protocol MongoDB server
        log.info("Starting lightweight pure-Java MongoDB Wire-Protocol Server (Bwaldvogel MongoServer)...");
        try {
            embeddedServer = new MongoServer(new MemoryBackend());
            try {
                // Attempt standard MongoDB port 27017 so MongoDB Compass connects immediately without special ports
                embeddedServer.bind("127.0.0.1", 27017);
                activePort = 27017;
            } catch (Exception portErr) {
                // If 27017 is occupied or restricted, bind to any available dynamic port
                embeddedServer.bind("127.0.0.1", 0);
                InetSocketAddress address = embeddedServer.getLocalAddress();
                activePort = address != null ? address.getPort() : 27017;
            }
            activeConnectionString = "mongodb://127.0.0.1:" + activePort + "/healthcaredb";
            log.info("MongoDB wire server active at: {}", activeConnectionString);
            return MongoClients.create(activeConnectionString);
        } catch (Exception e) {
            log.error("Failed to start embedded MongoDB wire server, falling back to localhost:27017", e);
            activeConnectionString = "mongodb://localhost:27017/healthcaredb";
            return MongoClients.create(activeConnectionString);
        }
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "healthcaredb");
    }

    private boolean isPortInUse(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @PreDestroy
    public void cleanup() {
        if (embeddedServer != null) {
            log.info("Shutting down embedded MongoDB wire server...");
            embeddedServer.shutdown();
        }
    }
}
