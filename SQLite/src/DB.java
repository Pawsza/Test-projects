
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {
	private Connection con = null;
	private Statement stmt = null;

	public boolean connect(String database) {
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			con.setAutoCommit(false);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Opened database successfully");
		return true;
	}

	public boolean createTable() {
		try {
			stmt = con.createStatement();
			String sql = "CREATE TABLE EMAILS " + "(EMAIL 	CHAR(50) PRIMARY KEY     NOT NULL," + " WEBSITE   TEXT     NOT NULL, " + " DATE      TEXT     NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Table created successfully");
		return true;
	}

	public boolean insert() {
		try {
			stmt = con.createStatement();

			String sql;

			for (int i = 4; i <= 1000000; i++) {
				sql = "INSERT INTO EMAILS (EMAIL, WEBSITE, DATE) " + "VALUES ('pawelek-91@o" + i + ".pl', 'www.o" + i + ".pl', CURRENT_TIMESTAMP);";
				stmt.executeUpdate(sql);
			}

			// sql = "INSERT INTO EMAILS (EMAIL, WEBSITE, DATE) " + "VALUES ('basia@gmail.com', 'www.google.pl', 'now');";
			// stmt.executeUpdate(sql);

			stmt.close();
			con.commit();
			con.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Records created successfully");
		return true;
	}

	public boolean select() {
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM EMAILS;");
			while (rs.next()) {
				System.out.println("Email = " + rs.getString("EMAIL"));
				System.out.println("Website = " + rs.getString("website"));
				System.out.println("Time = " + rs.getString("DATE"));
				System.out.println();
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Operation done successfully");
		return true;
	}

	public boolean disconnect() {
		try {
			con.close();
		} catch (Exception e) {
			System.err.println("Can't close connection");
			return false;
		}
		return true;
	}
}
