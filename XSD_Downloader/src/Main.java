public class Main {

	public static void main(String[] args) throws Exception {

		// allows validate files with more then 5k nodes occurs
		System.setProperty("jdk.xml.maxOccurLimit", "20000");


		SchemaPersister schemaPersister = new SchemaPersister();

		// schemaPersister.getSchema("http://crd.gov.pl/wzor/2016/01/05/3055/schemat.xsd", true);
		schemaPersister.doItForFile("XSD/Schemat_JPK_VAT(1)_v1-0.xsd");

		System.out.println("Finished!");
	}
}
