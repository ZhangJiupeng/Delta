package com.delta.core.util;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * This is a SimpleDataSource in {@code DataSource} standard.
 * it is base on lazy-load and support validationQuery to check
 * a connection if it is valid in its own life cycle when you are
 * going to use it.
 *
 * @author Jim Zhang
 * @version 2.1
 * @since 1.8
 */
public class SimpleDataSource implements javax.sql.DataSource {
    private static int poolSize;
    private static int minPoolSize;
    private static String url;
    private static String validationQuery;
    private transient static Properties properties;
    private transient static PrintWriter logWriter;
    private transient static ArrayList<Connection> connections;

    public SimpleDataSource(Properties properties) {
        SimpleDataSource.properties = properties;
        try {
            poolSize = Integer.parseInt(properties.getProperty("poolSize", "10"));
            minPoolSize = Integer.parseInt(properties.getProperty("minPoolSize", "2"));
            if (poolSize <= 0 || minPoolSize < 0) {
                throw new RuntimeException("poolSize/minPoolSize must be a positive number");
            }
            if (poolSize <= minPoolSize) {
                throw new RuntimeException("poolSize [" + poolSize + "] cannot be less then " +
                        "minPoolSize [" + minPoolSize + "]");
            }
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("parameter illegal: pool size must be number. "
                    + nfe.getMessage());
        }
        url = properties.getProperty("url");
        validationQuery = properties.getProperty("validationQuery", "select 1");
        if (properties.getProperty("showLog", "false").equals("true")) {
            logWriter = new PrintWriter(System.out);
        }
        String driver = properties.getProperty("driver", "driver not declared in jdbc.properties");
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found: " + e.getMessage());
        }
        try {
            connections = new ArrayList<>();
            for (int i = 0; i < poolSize; i++) {
                connections.add(DriverManager.getConnection(url, properties));
            }
            println("[REMIND]\t" + (new java.util.Date())
                    + "\t" + "Connection pool initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Get a connection from pool.<br/>
     * With life-cycle valid first. If it is out dated, it will
     * auto ensure the poolSize and try again (Lazy loading).
     */
    @Override
    public Connection getConnection() throws SQLException {
//        connections.forEach((e) -> {
//            System.out.print(e.toString().split("@")[1].substring(7) + "\t");
//        });
//        System.out.println();
        if (connections.size() == minPoolSize) {
            ensureCapacity();
        }
        Connection connection = connections.remove(0);
        if (connection.isClosed()) {
            return getConnection();
        }
        try {
            connection.prepareStatement(validationQuery).execute();
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException ce) {
            return getConnection();
        } catch (SQLSyntaxErrorException se) {
            throw new RuntimeException("You have an syntax error in your validationQuery @ jdbc.properties");
        }
        return connection;
    }

    /**
     * Put the connection back into the pool if the pool is not full.<br/>
     * If the pool is full, this connection will be closed at once.
     *
     * @param connection the connection needs to be recycled.
     */
    public synchronized void free(Connection connection) {
        if (connections.size() < poolSize) {
            connections.add(connection);
        } else {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Ensure capacity when connections not enough.<br/>
     * The pool size is always between minPoolSize and poolSize.
     */
    private synchronized void ensureCapacity() {
        for (int i = minPoolSize; i < poolSize; i++) {
            try {
                connections.add(DriverManager.getConnection(url, properties));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        println("[REMIND]\t" + (new java.util.Date())
                + "\t" + "Ensure capacity to " + poolSize + ".");
    }

    private static void println(String str) {
        if (logWriter != null) {
            logWriter.print(str + "\n");
            logWriter.flush();
        }
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        logWriter = out;
    }

    @Override
    @Deprecated
    // This method is no use in SimpleDataSource, so it will be replaced by getConnection()
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    @Deprecated
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    @Deprecated
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    @Deprecated
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    @Override
    @Deprecated
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    @Deprecated
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
