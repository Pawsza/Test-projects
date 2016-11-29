
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
			String sql = "CREATE TABLE EMAILS " + "(EMAIL 	CHAR(50) PRIMARY KEY     NOT NULL," + " WEBSITE   TEXT     NOT NULL, " + " DATE      TEXT     NOT NULL,"
					+ " COUNTS   integer     NOT NULL )";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Table created successfully");
		return true;
	}

	public synchronized boolean insert(String email, String webpage) {
		try {
			stmt = con.createStatement();

			String sql;

			sql = "INSERT INTO EMAILS (EMAIL, WEBSITE, DATE, COUNTS) " + "VALUES ('" + email + "', '" + webpage + "', CURRENT_TIMESTAMP , 1);";
			stmt.executeUpdate(sql);

			// sql = "INSERT INTO EMAILS (EMAIL, WEBSITE, DATE) " + "VALUES ('basia@gmail.com', 'www.google.pl', 'now');";
			// stmt.executeUpdate(sql);

			stmt.close();
			con.commit();
		} catch (Exception e) {
			try {
				stmt = con.createStatement();

				String sql = "UPDATE EMAILS set COUNTS = COUNTS + 1 where email = '" + email + "';";
				stmt.executeUpdate(sql);

				stmt.close();
				con.commit();
			} catch (Exception ee) {
				System.out.println("Insert ocurred errors");
				return false;
			}
		}
		System.out.println("Records created successfully");
		return true;
	}

	public boolean select() {
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM EMAILS;");
			while (rs.next()) {
				System.out.println(rs.getInt("Counts") + "\t" + rs.getString("EMAIL"));
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
