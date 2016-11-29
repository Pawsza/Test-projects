public class Main {

	public static void main(String[] args) {

		/**
		 * 1M records ~ 100 MB This table is witout repeats
		 */

		DB db = new DB();
		db.connect("emails");
		// db.createTable();
		db.insert();
		// db.select();
		db.disconnect();

	}

}
