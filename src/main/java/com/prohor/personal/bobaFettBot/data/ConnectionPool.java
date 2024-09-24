package com.prohor.personal.bobaFettBot.data;

import java.sql.*;
import java.util.*;

public class ConnectionPool {
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
        initializePool();
    }

    private void initializePool() throws SQLException {
        for (int i = 0; i < maxConnections; i++) {
            availableConnections.add(createConnection());
        }
    }

    private Connection createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        creationTime.put(connection, System.currentTimeMillis() / 1000);
        return connection;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (availableConnections.isEmpty()) {
            try {
                System.out.println("waiting connection...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (availableConnections.isEmpty())
                return createConnection();
        }
        return availableConnections.removeFirst();
    }

    public synchronized void releaseConnection(Connection connection) throws SQLException {
        if (creationTime.containsKey(connection) &&
                creationTime.get(connection) + secondsToLive > System.currentTimeMillis() / 1000 &&
                availableConnections.size() < maxConnections)
            availableConnections.addLast(connection);
        else {
            connection.close();
            if (availableConnections.size() < maxConnections)
                availableConnections.add(createConnection());
        }
    }
}
