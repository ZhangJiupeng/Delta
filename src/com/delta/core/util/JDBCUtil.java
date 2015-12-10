package com.delta.core.util;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class provides data-access methods between db and your program.
 * <br/> It needs support by jdbc drivers and a configuration file called
 * <b>jdbc.properties</b>.<br/> It given a connection pool and design in
 * {@code DataSource} standard. but unfortunately, it has NOT implement
 * {@code DataSource}.
 *
 * @author Jim Zhang
 * @version 1.2
 * @since 1.8
 */
@SuppressWarnings("ALL")
public final class JDBCUtil {
    private transient static ArrayList<Connection> connections;
    private transient static RowSetFactory rowSetFactory;
    private transient final static int poolSize;
    private transient final static int minPoolSize;
    private transient final static String url;
    private static PrintWriter logWriter;

    /**
     * This utility needs a related file <strong>jdbc.properties</strong><br/>
     * and <strong>DriverManager</strong> below can ask <strong>SystemClassLoader</strong>
     * to load the suitable jdbc dirver by iterator the Implements.
     *
     * @see DriverManager
     * @see Driver
     * @see com.mysql.jdbc.Driver
     * @see com.mysql.jdbc.ConnectionProperties
     */
    private transient static Properties properties;

    static {
        // DriverManager.setLogWriter(new PrintWriter(System.out));
        properties = new Properties();
        try (InputStream is = ClassLoader.getSystemResourceAsStream("jdbc.properties")) {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        int t_poolSize, t_minPoolSize;
        try {
            t_poolSize = Integer.parseInt(properties.getProperty("poolSize"));
            t_minPoolSize = Integer.parseInt(properties.getProperty("minPoolSize"));
        } catch (NumberFormatException nfe) {
            throw new NumberFormatException("parameter illegal: pool size must be number. "
                    + nfe.getMessage());
        }
        poolSize = t_poolSize == 0 ? 10 : t_poolSize;
        minPoolSize = t_minPoolSize == 0 ? 2 : t_minPoolSize;
        url = properties.getProperty("url");
        try {
            connections = new ArrayList<>();
            for (int i = 0; i < poolSize; i++) {
                connections.add(DriverManager.getConnection(url, properties));
            }
            rowSetFactory = RowSetProvider.newFactory();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Ensure capacity when connections not enough.<br/>
     * The pool size is always between minPoolSize and poolSize.
     */
    private static void ensureCapacity() {
        synchronized (connections) {
            for (int i = minPoolSize; i < poolSize; i++) {
                try {
                    connections.add(DriverManager.getConnection(url, properties));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        println("[REMIND]\t" + (new java.util.Date())
                + "\t" + "Ensure Capacity to " + poolSize + " now!");
    }

    /**
     * Get a connection from pool.<br/>
     * With life-cycle valid first. If it is out dated, it will
     * auto ensure the poolSize and try again (Lazy loading).
     */
    private static Connection getConnection() {
        synchronized (connections) {
            if (connections.size() == minPoolSize) {
                ensureCapacity();
            }
            Connection connection = connections.remove(0);
            try {
                connection.prepareStatement("select 1").execute();
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException ce) {
                return getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        }
    }

    /**
     * Put the connection back into the pool if the pool is not full.<br/>
     * If the pool is full, this connection will be closed at once.
     *
     * @param connection the connection needs to be recycled.
     */
    private static void free(Connection connection) {
        synchronized (connection) {
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
    }

    /**
     * Function is the same as it's name.
     *
     * @param ps  UnpreparedStatement
     * @param obj Object
     * @return PreparedStatement
     * @throws SQLException
     */
    private static void fillPreparedStatementParams(PreparedStatement ps, Object... obj) throws SQLException {
        for (int i = 0; i < obj.length; i++) {
            Object target = obj[i];
            if (target == null) {
                ps.setNull(i + 1, Types.JAVA_OBJECT);
            } else {
                ps.setObject(i + 1, target.getClass() == Character.class ?
                        String.valueOf(target) : target);
            }
        }
    }

    private static void println(String str) {
        if (logWriter != null) {
            logWriter.println(str);
            logWriter.flush();
        }
    }

    public static void setPrintStream(PrintStream ps) {
        JDBCUtil.logWriter = new PrintWriter(ps);
    }

    /**
     * Basic method for database update.
     *
     * @param sql structure query language.
     * @param obj objects for each '?' in your sql, it can be ignored.
     * @return count for how many rows updated.
     */
    public static int executeUpdate(String sql, Object... obj) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\t" + ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            free(connection);
        }
        return 0;
    }

    /**
     * Try this method if the updated rows may larger then Integer.MAX.
     *
     * @param sql structure query language.
     * @param obj objects for each '?' in your sql, it can be ignored.
     * @return count for how many rows updated.
     */
    public static long executeLargeUpdate(String sql, Object... obj) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\t" + ps);
            return ps.executeLargeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            free(connection);
        }
        return 0;
    }

    /**
     * Use this method if you need a transition process for same sql.<br/>
     * This method is synchronized and support rollback.
     *
     * @param sql     structure query language for different conditions.
     * @param objects a array which contains sql '?' patterns.
     * @return count array of how many rows updated for each result.
     */
    public synchronized static int[] executeBatch(String sql, ArrayList<Object[]> objects) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (Object[] obj : objects) {
                if (obj != null) {
                    fillPreparedStatementParams(ps, obj);
                }
                ps.addBatch();
            }
            int[] affectedRows = ps.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            return affectedRows;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            free(connection);
        }
        return new int[objects.size()];
    }

    /**
     * Use this method if you need a transition process for same sql.<br/>
     * This method is synchronized and support rollback.<br/>
     * This method return long[].
     *
     * @param sql     structure query language for different conditions.
     * @param objects a array which contains sql '?' patterns.
     * @return count array of how many rows updated for each result.
     */
    public synchronized static long[] executeLargeBatch(String sql, ArrayList<Object[]> objects) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (Object[] obj : objects) {
                fillPreparedStatementParams(ps, obj);
                ps.addBatch();
            }
            long[] affectedRows = ps.executeLargeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            return affectedRows;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            free(connection);
        }
        return new long[objects.size()];
    }

    /**
     * Try this method if you need a batch sql process.
     * This method is synchronized and support rollback.
     *
     * @param tasks map for sql-patterns entry.
     * @return count array of how many rows updated for each result.
     */
    public synchronized static int[] executeBatch(HashMap<String, Object[]> tasks) {
        Connection connection = getConnection();
        int[] affectedRows = new int[tasks.size()];
        // It is a final reference so that it value can be updated in the lambda block below.
        final int[] cur = {0};
        try {
            connection.setAutoCommit(false);
            tasks.forEach((sql, obj) -> {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    connection.setAutoCommit(false);
                    if (obj != null) {
                        fillPreparedStatementParams(ps, obj);
                    }
                    affectedRows[cur[0]++] = ps.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            free(connection);
        }
        return affectedRows;
    }

    /**
     * Try this method to query from database by sql.
     *
     * @param sql structure query language.
     * @param obj objects for each '?' in your sql, it can be ignored.
     * @return CachedRowSet, It is a line-off result but <b>NEED TO BE CLOSED</b>.
     */
    public static CachedRowSet executeQuery(String sql, Object... obj) {
        Connection connection = getConnection();
        CachedRowSet cachedRowSet = null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\t" + ps);
            ResultSet resultSet = ps.executeQuery();
            cachedRowSet = rowSetFactory.createCachedRowSet();
            cachedRowSet.populate(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            free(connection);
        }
        return cachedRowSet;
    }

    /**
     * Try this method to query or update from database by sql.
     *
     * @param sql structure query language.
     * @param obj objects for each '?' in your sql, it can be ignored.
     * @return true means the execute is affected.
     */
    public static boolean execute(String sql, Object... obj) {
        Connection connection = getConnection();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\t" + ps);
            return ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            free(connection);
        }
        return false;
    }
}

