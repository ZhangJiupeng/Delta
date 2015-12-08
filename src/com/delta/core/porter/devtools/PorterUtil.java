package com.delta.core.porter.devtools;

import com.delta.core.util.JDBCUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ALL")
public final class PorterUtil {

    public static void createBeanByTableReference(String tableName) throws IOException {
        if (tableName == null || tableName.length() == 0) {
            return;
        }
        createBeanByTableReference(tableName, tableName.substring(0, 1).toUpperCase()
                + tableName.substring(1).toLowerCase() + ".java");
    }

    public static void createBeanByTableReference(String tableName, String outPath) throws IOException {
        if (tableName == null || tableName.length() == 0) {
            return;
        }
        String beanName = tableName.substring(0, 1).toUpperCase()
                + tableName.substring(1).toLowerCase();
        Map<String, String> paramMap = new HashMap<>();
        try (ResultSet rs = JDBCUtil.executeQuery("desc " + tableName)) {
            while(rs.next()) {
                System.out.println(rs.getString(1));
                paramMap.put(rs.getString(1),
                        getSimpleTypeString(rs.getString(2).toLowerCase()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
        writer.newLine();
        writer.write("// This Class is auto generated, some packages need to be imported.");
        writer.newLine();
        writer.write("@Entity(\"" + tableName + "\")");
        writer.newLine();
        writer.write("public class " + beanName + " {");
        writer.newLine();
        Set<String> keySet = paramMap.keySet();
        for (String paramName : keySet) {
            writer.write("\tprivate " + paramMap.get(paramName) + " " + paramName + ";");
            writer.newLine();
        }
        writer.newLine();
        writer.write("\tpublic " + beanName + "() {");
        writer.newLine();
        writer.newLine();
        writer.write("\t}");
        writer.newLine();
        for (String paramName : keySet) {
            writer.newLine();
            writer.write("\tpublic " + paramMap.get(paramName) + " get"
                    + paramName.substring(0, 1).toUpperCase() + paramName.substring(1) + "() {");
            writer.newLine();
            writer.write("\t\treturn " + paramName + ";");
            writer.newLine();
            writer.write("\t}");
            writer.newLine();
            writer.newLine();
            writer.write("\tpublic void set"
                    + paramName.substring(0, 1).toUpperCase() + paramName.substring(1) + "("
                    + paramMap.get(paramName) + " " + paramName + ") {");
            writer.newLine();
            writer.write("\t\tthis." + paramName  + " = " + paramName + ";");
            writer.newLine();
            writer.write("\t}");
            writer.newLine();
        }
        writer.write("}");
        writer.flush();
        writer.close();
    }

    private static String getSimpleTypeString(String dbType) {
        if (dbType.startsWith("int")) { return "int"; }
        else if (dbType.startsWith("double")) { return "double"; }
        else if (dbType.startsWith("char")) { return "String"; }
        else if (dbType.startsWith("varchar")) { return "String"; }
        else if (dbType.startsWith("nvarchar")) { return "String"; }
        else if (dbType.startsWith("nchar")) { return "String"; }
        else if (dbType.startsWith("text")) { return "String"; }
        else if (dbType.startsWith("bit")) { return "boolean"; }
        else if (dbType.startsWith("binary")) { return "byte[]"; }
        else if (dbType.startsWith("image")) { return "byte[]"; }
        else if (dbType.startsWith("real")) { return "float"; }
        else if (dbType.startsWith("bigint")) { return "long"; }
        else if (dbType.startsWith("tinyint")) { return "short"; }
        else if (dbType.startsWith("smallint")) { return "short"; }
        else if (dbType.startsWith("decimal")) { return "java.math.BigDecimal"; }
        else if (dbType.startsWith("numeric")) { return "java.math.BigDecimal"; }
        else if (dbType.startsWith("datetime")) { return "java.sql.Times"; }
        else { return "Object"; }
    }
}
