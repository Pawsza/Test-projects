import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SchemaPersister {
	private static String EXPORT_FILESYSTEM_ROOT = "XML/";

	private static CredentialsProvider credsProvider;
	private static CloseableHttpClient client;
	private static HttpClientContext context;

    // some caching of the http-responses
    private static Map<String,String> _httpContentCache = new HashMap<String,String>();
	private static final String PROXY_HOST = "";
	private static final String PROXY_DOMAIN = "";
	private static final int PROXY_PORT = 0;
	private static final String ProxyUserName = "";
	private static final String ProxyPassword = "";

	public SchemaPersister() throws KeyManagementException, NoSuchAlgorithmException {


		credsProvider = web.prepareCredentials(PROXY_HOST, PROXY_DOMAIN, PROXY_PORT, ProxyUserName, ProxyPassword);
		client = web.prepareServiceClient(credsProvider, PROXY_HOST, PROXY_PORT, ProxyUserName);

		AuthCache authCache = new BasicAuthCache();
		context = HttpClientContext.create();
		if (credsProvider != null) {
			context.setCredentialsProvider(credsProvider);
		}
		context.setAuthCache(authCache);
	}

	public Schema getSchema(String url, boolean download) {

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// without proxy
		if (PROXY_HOST.equals("") && !download) {
			try {
				return factory.newSchema(new URL(url));
			} catch (Exception e) {
				System.err.println("Can't get scheme online");
				System.err.println("Link to scheme " + url);
				e.printStackTrace();
			}
		}

		String schemaLocation = getLocalName(url);

		File schemaFile = null;
		try {
			schemaFile = new File(EXPORT_FILESYSTEM_ROOT + schemaLocation);

			// with proxy
			if (schemaFile.exists())
				return factory.newSchema(new File(EXPORT_FILESYSTEM_ROOT + schemaLocation));
			else {
				doIt(url);
				return factory.newSchema(new File(EXPORT_FILESYSTEM_ROOT + schemaLocation));
			}
		} catch (Exception e) {
			System.err.println("Can't get scheme from local storage");
			System.err.println("Local scheme path: " + schemaFile.getPath());
			e.printStackTrace();
			return null;
		}
	}

	private String getLocalName(String url) {
		if (url.startsWith("http")) {

			url = url.replace("http://", "");
			url = url.replace("/", "_");

		} else {

			url = url.replace("/", "_");
			url = url.replace("\\", "_");

		}
		return url;
    }

	private void doIt(String url) throws Exception {

        Set <SchemaElement> allElements = new HashSet<SchemaElement>() ;

		allElements.add(new SchemaElement(new URL(url)));

		// ------- XSD Files can be also pass to future proccesing -------
        //URL url = new URL("http://jpk.mf.gov.pl/wzor/2016/03/09/03094/Schemat_JPK_VAT(1)_v1-0.xsd");
        
		// ------- Directory with XSD Files can be also pass to future proccesing -------
		/*
		 * File folder = new File("C:\\temp\\src"); for (final File fileEntry : folder.listFiles()) { if (!fileEntry.isDirectory()) { URL url = new URL("file:/C:/temp/src/"+fileEntry.getName()); allElements.add ( new SchemaElement(url)); System.out.println("Add element: "+ fileEntry.getPath()); } }
		 */

		// processing each elements
        for (SchemaElement e: allElements) {
            e.doAll();
        }
    }

	public void doItForFile(String FilePath) throws Exception {

		Set<SchemaElement> allElements = new HashSet<SchemaElement>();

		File dir = new File(FilePath);
		if (dir.isDirectory()) {

		} else {
			allElements.add(new SchemaElement(new URL("file:" + dir.getAbsolutePath())));
			System.out.println("Added: " + dir.getAbsolutePath());
		}

		for (SchemaElement e : allElements) {
			e.doAll();
		}
	}

    class SchemaElement {

        private URL    _url;
        private String _content;

        public List <SchemaElement> _imports ;
        public List <SchemaElement> _includes ;

        public SchemaElement(URL url) {
            this._url = url;
        }


        public void checkIncludesAndImportsRecursive() throws Exception {

        	InputStream in = new ByteArrayInputStream(downloadContent().getBytes("UTF-8"));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

			try {
				Document doc = builder.parse(in);
				List<Node> includeNodeList = null;
				List<Node> importNodeList = null;

				includeNodeList = getXpathAttribute(doc, "/*[local-name()='schema']/*[local-name()='include']");
				_includes = new ArrayList<SchemaElement>();

				for (Node element : includeNodeList) {

					Node sl = element.getAttributes().getNamedItem("schemaLocation");
					if (sl == null) {
						// defines import but no schemaLocation
						continue;
					}

					String asStringAttribute = sl.getNodeValue();

					URL url = buildUrl(asStringAttribute, _url);

					SchemaElement tmp = new SchemaElement(url);
					tmp.setSchemaLocation(asStringAttribute);

					tmp.checkIncludesAndImportsRecursive();
					_includes.add(tmp);
                }

				importNodeList = getXpathAttribute(doc, "/*[local-name()='schema']/*[local-name()='import']");
				_imports = new ArrayList<SchemaElement>();

				for (Node element : importNodeList) {

					Node sl = element.getAttributes().getNamedItem("schemaLocation");
					if (sl == null) {
						// defines import but no schemaLocation
						continue;
					}

					String asStringAttribute = sl.getNodeValue();
					URL url = buildUrl(asStringAttribute, _url);

					SchemaElement tmp = new SchemaElement(url);
					tmp.setSchemaLocation(asStringAttribute);

					tmp.checkIncludesAndImportsRecursive();

					_imports.add(tmp);
				}

				in.close();

			} catch (Exception e) {
				System.err.println("PROBLEM WITH DOWNLOADED HTML CONTENT IN VALIDATOR: ");
				System.err.println(_content);
				e.printStackTrace();
            }

        }   
        
        private String schemaLocation;

        private void setSchemaLocation(String schemaLocation) {
            this.schemaLocation = schemaLocation;

        }

        private URL buildUrl(String asStringAttribute, URL parent) throws Exception {

            if (asStringAttribute.startsWith("http")) {
                return new URL(asStringAttribute);
            }

            if (asStringAttribute.startsWith("file")) {
                return new URL(asStringAttribute);
            }

            // relative URL
            URI parentUri = parent.toURI().getPath().endsWith("/") ? parent.toURI().resolve("..") : parent.toURI().resolve(".");
            return new URL(parentUri.toURL().toString() + asStringAttribute );

        }
        
        public void doAll() throws Exception {
        	
			// READ ELEMENTS
            checkIncludesAndImportsRecursive();

			// GENERATE OUTPUT
            patchAndPersistRecursive(0);

        }
        
        public void patchAndPersistRecursive(int level) throws Exception {

			File f = new File(EXPORT_FILESYSTEM_ROOT + this.getXDSName());

            if (_imports.size() > 0) {

				// IMPORTS
                for (SchemaElement kid : _imports) {
                    kid.patchAndPersistRecursive(level+1);
                }

            }

            if (_includes.size() > 0) {

				// INCLUDES
                for (SchemaElement kid : _includes) {
                    kid.patchAndPersistRecursive(level+1);
                }

            }
            
            String contentTemp = downloadContent();

            for (SchemaElement i : _imports ) {

                if (i.isHTTP()) {
                	String tmp1 = "schemaLocation=\"" + i.getSchemaLocation();        
                	String tmp2 = "schemaLocation=\"./" + i.getXDSName();
                    contentTemp = contentTemp.replace(tmp1, tmp2);
                }

            }


            for (SchemaElement i : _includes ) {

                if (i.isHTTP()) {
                	String tmp1 = "schemaLocation=\"" + i.getSchemaLocation();
                	String tmp2 = "schemaLocation=\"./" + i.getXDSName();
                    contentTemp = contentTemp.replace(tmp1, tmp2);
                }

            }


            FileOutputStream fos = new FileOutputStream(f);     
            fos.write(contentTemp.getBytes("UTF-8"));
            fos.close();

        }

        String getSchemaLocation() {
            return schemaLocation;
        }

        private String getXDSName() {

            String tmp = schemaLocation;

            // Root on local File-System -- just grap the last part of it
            if (tmp == null) {
				// short name XSD file
				// tmp = _url.toString().replaceFirst(".*/([^/?]+).*", "$1");

				// long name XSD file (with rest of link)
				tmp = _url.toString();
            }


            if ( isHTTP() ) {

                tmp = tmp.replace("http://", "");
                tmp = tmp.replace("/", "_");

            } else {

                tmp = tmp.replace("/", "_");
                tmp = tmp.replace("\\", "_");
				tmp = _url.toString().replaceFirst(".*/([^/?]+).*", "$1");

            }

            return tmp;

        }

        private boolean isHTTP() {
            return _url.getProtocol().startsWith("http");
        }

        private String downloadContent() throws Exception {

            if (_content == null) {

				// reading content from _url
				// check cache (avoids multiple download one file)
                if (_httpContentCache.containsKey(_url.toString())) {
                    this._content = _httpContentCache.get(_url.toString());
                } else {

					// handle http
					if (_url.toString().startsWith("http")) {
						HttpGet get = new HttpGet(_url.toString());
						HttpResponse response = client.execute(get, context);

						_content = EntityUtils.toString(response.getEntity(), "UTF-8");
					}
					// handle file
					else {
						File fileDir = new File(_url.getFile());

						if (fileDir.exists()) {
							BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF-8"));
							String str;

							while ((str = in.readLine()) != null) {
								if (_content == null) {
									_content = str;
								} else {
									_content += str;
								}
							}
						}
					}

                    if (this._content != null) {
                        _httpContentCache.put(_url.toString(), this._content);
                    }

                }

            }

            if (_content == null) {
                throw new NullPointerException("Content of " + _url.toString() + "is null ");
            }

            return _content;

        }

        private List<Node> getXpathAttribute(Document doc, String path) throws Exception {

            List <Node> returnList = new ArrayList <Node> ();

            XPathFactory xPathfactory = XPathFactory.newInstance();

            XPath xpath = xPathfactory.newXPath();

            {
                XPathExpression expr = xpath.compile(path);

                NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET );

                for (int i = 0 ; i < nodeList.getLength(); i++) {

                    Node n = nodeList.item(i);

                    returnList.add(n);

                }
            }

            return returnList;

        }

        @Override
        public String toString() {

            if (_url != null) {
                return _url.toString();
            }

            return super.toString();

        }

    }
}