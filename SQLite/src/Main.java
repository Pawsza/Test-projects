public class Main {

	public static void main(String[] args) throws InterruptedException {

		/**
		 * 1M records ~ 100 MB This table is witout repeats
		 */

		long startTime = System.currentTimeMillis();

		DB db = new DB();
		db.connect("emails");
		// db.createTable();
		db.insert("basia@o2.pl", "www.onet.pl");
		db.select();
		db.disconnect();

		System.out.println("Zrealizowano w: " + (System.currentTimeMillis() - startTime) / 1000f + "s");
	}

}
