package com.delta.core.util;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * This class provides data-access methods between db and your program.
 * <br/> It needs support by jdbc drivers, a data source and a configuration
 * file called <b>jdbc.properties</b>.<br/> A SimpleDataSource is given and
 * implements {@code DataSource}. it also support other data sources like
 * c3p0, dbcp or new tomcat-jdbc and so on.
 *
 * @author Jim Zhang
 * @version 2.1
 * @since 1.8
 */
public final class JDBCUtil {
    private transient static DataSource dataSource;
    private transient static PrintWriter logWriter;
    private transient static RowSetFactory rowSetFactory;

    static {
        try {
            rowSetFactory = RowSetProvider.newFactory();
            Properties properties = new Properties();
            String resourceName = "jdbc.properties";
            URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resourceName);
            if (resourceUrl == null) {
                resourceUrl = JDBCUtil.class.getClassLoader().getResource(resourceName);
            }
            assert resourceUrl != null;
            try (InputStream is = new FileInputStream(resourceUrl.getPath())) {
                properties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
            if (properties.getProperty("showLog", "false").equals("true")) {
                setPrintStream(System.out);
            }
            if (properties.getProperty("useCustomDataSource", "false").equals("false")) {
                setDataSource(new SimpleDataSource(properties));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // It is only a static class and no need to use by a object instance.
    private JDBCUtil() {
    }

    public static void setPrintStream(PrintStream ps) {
        logWriter = new PrintWriter(ps);
    }

    public static void setDataSource(DataSource customDataSource) {
        if (dataSource != null) {
            throw new RuntimeException("DataSource has already exists ["
                    + JDBCUtil.dataSource.getClass() + "] which cannot be modified. ["
                    + customDataSource.getClass() + "] (if you need a custom DataSource," +
                    " please declare in useCustomDataSource @ jdbc.properties)");
        } else {
            dataSource = customDataSource;
        }
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
        assert connection != null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\tExecute SQL ["
                    + ps.toString().substring(ps.toString().indexOf(": ") + 2) + "]");
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dataSource.getClass() == SimpleDataSource.class) {
                ((SimpleDataSource) dataSource).free(connection);
            }
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
        assert connection != null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\tExecute SQL ["
                    + ps.toString().substring(ps.toString().indexOf(": ") + 2) + "]");
            return ps.executeLargeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dataSource.getClass() == SimpleDataSource.class) {
                ((SimpleDataSource) dataSource).free(connection);
            }
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
        assert connection != null;
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
            if (dataSource.getClass() == SimpleDataSource.class) {
                ((SimpleDataSource) dataSource).free(connection);
            }
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
        assert connection != null;
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
            if (dataSource.getClass() == SimpleDataSource.class) {
                ((SimpleDataSource) dataSource).free(connection);
            }
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
        assert connection != null;
        int[] affectedRows = new int[tasks.size()];
        // It is a final reference so that itsd value can be updated in the lambda block below.
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
            if (dataSource.getClass() == SimpleDataSource.class) {
                ((SimpleDataSource) dataSource).free(connection);
            }
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
        assert connection != null;
        CachedRowSet cachedRowSet = null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\tExecute SQL ["
                    + ps.toString().substring(ps.toString().indexOf(": ") + 2) + "]");
            ResultSet resultSet = ps.executeQuery();
            cachedRowSet = rowSetFactory.createCachedRowSet();
            cachedRowSet.populate(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dataSource.getClass() == SimpleDataSource.class) {
                ((SimpleDataSource) dataSource).free(connection);
            }
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
        assert connection != null;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillPreparedStatementParams(ps, obj);
            println("[REMIND]\t" + (new java.util.Date()) + "\tExecute SQL ["
                    + ps.toString().substring(ps.toString().indexOf(": ") + 2) + "]");
            return ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (dataSource.getClass() == SimpleDataSource.class) {
                ((SimpleDataSource) dataSource).free(connection);
            }
        }
        return false;
    }

    /**
     * Function is the same as it's name.
     *
     * @param ps  UnpreparedStatement
     * @param obj Object
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
            logWriter.print(str + "\n");
            logWriter.flush();
        }
    }

    private static Connection getConnection() {
        if (dataSource == null) {
            throw new NullPointerException("DataSource not found.");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

