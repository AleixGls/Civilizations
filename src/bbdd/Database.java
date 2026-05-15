package bbdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// This class manages the connection to the DDBB and pass Connection object to the other database classes
public class Database  {
    private Connection conn;

    public static void main(String[] args) {

    	String url = "jdbc:mysql://civilizations2526.c92s82e2qeo2.eu-north-1.rds.amazonaws.com/civilizations2526?useSSL=false&allowPublicKeyRetrieval=true";
    	String username = "admin";
        String pass = "Jefecolorado123";


        Database db = new Database(url, username, pass);
        System.out.println(db.isClosed());
        db.close();
        System.out.println(db.isClosed());
    }

    public Database(String url, String username, String pass) {
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
            // System.out.println("Driver loaded");
            conn = DriverManager.getConnection(url, username, pass);
            // System.out.println("Connection established");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Driver could not be loaded...");
            e.printStackTrace();
        }
    }
    
    public void close() {
    	try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean isClosed() {
    	try {
			return conn.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
    }

    public Connection getConnection() {
        return conn;
    }

}