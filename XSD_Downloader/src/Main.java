public class Main {

	public static void main(String[] args) throws Exception {

		System.setProperty("jdk.xml.maxOccurLimit", "20000");

		SchemaPersister schemaPersister = new SchemaPersister();
		schemaPersister.getSchema("http://crd.gov.pl/wzor/2016/01/05/3055/schemat.xsd");
		System.out.println("jakies testowe zmiany");
	}
}
