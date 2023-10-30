/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Conex;
import java.sql.*;
/**
 *
 * @author jabes
 */
public class Conexion {
    //Valoresdeconexion a MySql
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //El puertoesopcional
    private static String JDBC_URL = "jdbc:mysql://localhost:3306/mediateca";
    private static String JDBC_USER = "root";
    private static String JDBC_PASS = "";
    private static Driver driver = null;
    
    //Para que no haya problemas al obtener la conexion de
    //manera concurrente, seu sa la palabra synchronized
    public static synchronized Connection getConnection()
    throws SQLException {
    if (driver == null) {
    try {
    
    //Se registra el driver
    Class jdbcDriverClass = Class.forName(JDBC_DRIVER);
    driver = (Driver) jdbcDriverClass.newInstance();
    DriverManager.registerDriver(driver);
    } catch (Exception e) {
    System.out.println("Fallo en cargar el driver JDBC");
    e.printStackTrace();
    }
    }
    return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }
    
    //Cierre del resultSet
    public static void close(ResultSet rs) {
    try {
    if (rs != null) {
    rs.close();
    }
    } catch (SQLException sqle) {
    sqle.printStackTrace();
    }
    }
    
    //Cierre del PrepareStatement
    public static void close(PreparedStatement stmt) {
    try {
    if (stmt != null) {
    stmt.close();
    }
    } catch (SQLException sqle) {
    sqle.printStackTrace();
    }
    }
    
    
    public static void close(Connection conn) {//Cierre de la conexion
    try {
    if (conn != null) {
    conn.close();
    }
    } catch (SQLException sqle) {
    sqle.printStackTrace();
    }
    }
}
