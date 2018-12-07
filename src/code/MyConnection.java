package code;

import java.sql.*;

/**
 * Clase que crear las Base de datos cuando es necesario y ademas contiene la Conexion
 * Para el uso en otras clases
 * @author SrExtibax
 */

public class MyConnection {
    
    private static Connection conn = null;
   
    public static Connection get() throws SQLException, ClassNotFoundException {
        
        if (conn == null) {
            
            try {
                
                conn = DriverManager.getConnection("jdbc:sqlite:DB.s3db");
                
            }catch (SQLException ex) {
                
                throw new SQLException(ex);
                
            }
        }
        return conn;
    }
    public static void close() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
    
    /**
     * Metodo que crea la Base de datos de los clientes de ser necesario
     * @param conexion 
     */
   
    public static void DatabaseCustomers(Connection conexion){  
        
        String sql = "CREATE TABLE IF NOT EXISTS customers (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name varchar NOT NULL,\n"
                + "	ruc varchar,\n"
                + "	repLegal varchar,\n"
                + "	nombreContactoEmpresa varchar,\n"
                + "	correoContactoEmpresa varchar,\n"
                + "	numeroContacto varchar,\n"
                + "	cedulaRepLegal varchar,\n"
                + "	nit varchar,\n"
                + "	noContribuyenteMuniPa varchar,\n"
                + "	contraMuniPa varchar\n"
                + ");";
        String url = "jdbc:sqlite:DB.s3db";
        
        try{
            
            Statement stmt = conexion.createStatement();
            stmt.execute(sql);
            
        } catch (SQLException e){
            
            System.out.println(e.getMessage());
            
        }
    }
    
    /**
     * Metodo que crea la base de datos de los Pdf de ser necesario
     * @param conexion 
     */
    
    public static void DatabasePdf(Connection conexion){
    
        String sql = "CREATE TABLE IF NOT EXISTS PDF (\n"
            + "	id INTEGER PRIMARY KEY,\n"
            + "	nombreclienteid varchar,\n"
            + "	nombrepdf varchar,\n"
            + "	archivopdf mediumblob\n"
            + ");";
        String url = "jdbc:sqlite:DB.s3db";

        try{
            
            Statement stmt = conexion.createStatement();
            stmt.execute(sql);
            
        } catch (SQLException e) {
            
        System.out.println(e.getMessage());
        
        }
    
    }
    
    /**
     * Metodo que crea la base de datos de las obligaciones fiscales
     * @param conexion 
     */
    
    public static void DatabaseObligation(Connection conexion){  
        
        String sql = "CREATE TABLE IF NOT EXISTS Obligaciones (\n"
                + "	id INTEGER PRIMARY KEY,\n"
                + "	nombreclienteid varchar,\n"
                + "	nombrecliente varchar,\n"
                + "	obligacion varchar,\n"
                + "	fecha varchar,\n"
                + "	hora varchar,\n"
                + "	send varchar\n"
                + ");";
        String url = "jdbc:sqlite:DB.s3db";
        
        try {
            
            Statement stmt = conexion.createStatement();
            stmt.execute(sql);
            
        } catch (SQLException e) {
            
            System.out.println(e.getMessage());
            
        }
        
    }
   
}
