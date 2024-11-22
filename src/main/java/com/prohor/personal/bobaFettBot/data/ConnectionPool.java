package com.prohor.personal.bobaFettBot.data;

import org.slf4j.*;

import java.sql.*;
import java.util.*;

public class ConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    private final LinkedList<Connection> availableConnections;
    private final Map<Connection, Long> creationTime;
    private final long secondsToLive;
    private final String url;
    private final String user;
    private final String password;
    private final int maxConnections;

    public ConnectionPool(String url, String user, String password, int maxConnections, long secondsToLive)
            throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxConnections = maxConnections;
        this.secondsToLive = secondsToLive;
        availableConnections = new LinkedList<>();
        creationTime = new HashMap<>();
        log.info("initialized connections pool(url={}, max={}, secondsToLive={})", url, maxConnections, secondsToLive);
        initializePool();
    }

    private void initializePool() throws SQLException {
        for (int i = 0; i < maxConnections; i++) {
            availableConnections.add(createConnection());
        }
    }

    private Connection createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        log.trace("pool size before creation: {}", availableConnections.size());
        log.trace("create new connection");
        creationTime.put(connection, System.currentTimeMillis() / 1000);
        return connection;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (availableConnections.isEmpty()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (availableConnections.isEmpty()) {
                log.trace("get new created connection");
                return createConnection();
            }
        }
        log.trace("get existing connection");
        return availableConnections.removeFirst();
    }

    public synchronized void releaseConnection(Connection connection) throws SQLException {
        if (creationTime.containsKey(connection) &&
                creationTime.get(connection) + secondsToLive > System.currentTimeMillis() / 1000 &&
                availableConnections.size() < maxConnections) {
            availableConnections.addLast(connection);
            log.trace("released connection saved");
        } else {
            connection.close();
            log.trace("released connection close");
            if (availableConnections.size() < maxConnections)
                availableConnections.add(createConnection());
        }
    }
}
