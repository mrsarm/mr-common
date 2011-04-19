package mr.common.format;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Funciones útiles para el manejo de contenidos XML.
 * @author Mariano Ruiz
 */
public abstract class XmlUtils {

	public static final String XMLSCHEMA_ID =
		"http://www.w3.org/2001/XMLSchema";
	public static final String SCHEMALANGUAGE_ID =
		"http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String SCHEMASOURCE_ID =
		"http://java.sun.com/xml/jaxp/properties/schemaSource";

	/**
	 * Parsea un documento XML a un objeto {@link org.w3c.dom.Document Document},
	 * antes valida con un esquema XML (XSD) el documento, y lanza una excepción
	 * en caso de fallar la validación.
	 *
	 * @param xml - Archivo que contiene el documento XML a parsear
	 * @param xmlSchema - Archivo que contiene el esquema que define
	 * el formato válido para validar el XML (archivo XSD).
	 * @return {@link org.w3c.dom.Document} DOM del XML
	 * @throws SAXException Si hay un error en la validación.
	 * @throws IOException Si se produce un error al obtener los archivos
	 * o parsear el documento
	 */
	public static Document getAndValidateDocument(File xml, File xmlSchema)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setAttribute(SCHEMALANGUAGE_ID, XMLSCHEMA_ID);
		factory.setAttribute(SCHEMASOURCE_ID, xmlSchema);

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		documentBuilder.setErrorHandler(new ErrorHandler() {
			public void warning(SAXParseException ex) throws SAXException {
				throw ex;
			}
			public void error(SAXParseException ex) throws SAXException {
				throw ex;
			}
			public void fatalError(SAXParseException ex) throws SAXException {
				throw ex;
			}
		});
		return documentBuilder.parse(xml);
	}

	/**
	 * Parsea un documento XML a un objeto {@link org.w3c.dom.Document Document},
	 * antes valida con un esquema XML (XSD) el documento, y lanza una excepción
	 * en caso de fallar la validación.
	 *
	 * @param xml - Archivo que contiene el documento XML a parsear
	 * @param xmlSchema - Archivo que contiene el esquema que define
	 * el formato válido para validar el XML (archivo XSD).
	 * @return {@link org.w3c.dom.Document} DOM del XML
	 * @throws SAXException Si hay un error en la validación.
	 * @throws IOException Si se produce un error en el parseo
	 */
	public static Document getAndValidateDocument(String xml, String xmlSchema)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setAttribute(SCHEMALANGUAGE_ID, XMLSCHEMA_ID);
		factory.setAttribute(SCHEMASOURCE_ID, xmlSchema);

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		documentBuilder.setErrorHandler(new ErrorHandler() {
			public void warning(SAXParseException ex) throws SAXException {
				throw ex;
			}
			public void error(SAXParseException ex) throws SAXException {
				throw ex;
			}
			public void fatalError(SAXParseException ex) throws SAXException {
				throw ex;
			}
		});
		return documentBuilder.parse(new InputSource(new StringReader(xml)));
	}

	/**
	 * Parsea un documento XML a un objeto {@link org.w3c.dom.Document Document}.
	 *
	 * @param xml - Archivo que contiene el documento XML a parsear
	 * @param xmlSchema - Archivo que contiene el esquema que define
	 * el formato válido para validar el XML (archivo XSD).
	 * @return {@link org.w3c.dom.Document} DOM del XML
	 * @throws ParserConfigurationException 
	 * @throws SAXException Si hay un error en la validación.
	 * @throws IOException Si se produce un error al obtener el archivo
	 * o al parsearlo
	 */
	public static Document getDocument(File xml)
	  throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		return documentBuilder.parse(xml);
	}

	/**
	 * Parsea un documento XML a un objeto {@link org.w3c.dom.Document Document}.
	 *
	 * @param xml - Archivo que contiene el documento XML a parsear
	 * @param xmlSchema - Archivo que contiene el esquema que define
	 * el formato válido para validar el XML (archivo XSD).
	 * @return {@link org.w3c.dom.Document} DOM del XML
	 * @throws ParserConfigurationException 
	 * @throws SAXException Si hay un error en la validación.
	 * @throws IOException Si se produce un error al obtener el archivo
	 * o al parsearlo
	 */
	public static Document getDocument(String xml)
	  throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		return documentBuilder.parse(new InputSource(new StringReader(xml)));
	}

	/**
	 * Retorna el mismo nodo si no es de tipo comentario
	 * o texto llano, sino retorna el
	 * siguiente nodo.
	 * @param node {@link org.w3c.dom.Node}
	 * @return {@link org.w3c.dom.Node}
	 */
	public static Node nextNode(Node node) {
		return isTagNode(node) ? node : node.getNextSibling();
	}

	/**
	 * @return <code>true</code> si el nodo no es un
	 * comentario ni un texto llano.
	 */
	public static boolean isTagNode(Node node) {
		return node.getNodeType() != Node.TEXT_NODE
		        && node.getNodeType() != Node.COMMENT_NODE;
	}

    /**
     * Parsea un tag con el nombre <code>tagName</code>,
     * y le agrega las properties pasadas en <code>properties</code>.
     * @param tagName nombre del tag
     * @param properties array de properties, cada una puede ser
     * parseada con {@link #prop(String, String)}
     * @return el tag XML parseado con los valores
     */
    public static String tag(String tagName, String [] properties) {
    	StringBuffer buffer = new StringBuffer(50);
    	buffer.append("<");
    	buffer.append(tagName);
    	buffer.append(" ");
    	for(int i=0; i<properties.length; i++) {
    		buffer.append(properties[i]);
    	}
    	buffer.append(">");
    	return buffer.toString();
    }

    /**
     * Parsea un tag con el nombre <code>tagName</code>.
     * @param tagName nombre del tag
     * @return el tag XML parseado con los valores
     */
    public static String tag(String tagName) {
    	return "<" + tagName + ">";
    }

    /**
     * Parsea un tag con el nombre <code>tagName</code>,
     * y le agrega las properties pasadas en <code>properties</code>.
     * @param tagName nombre del tag
     * @param property valor propiedad del tag, puede ser
     * parseado con {@link #prop(String, String)}, o
     * puede ser <code>null</code>
     * @return el tag XML parseado
     */
    public static String tag(String tagName, String property) {
    	StringBuffer buffer = new StringBuffer(50);
    	buffer.append("<");
    	buffer.append(tagName);
    	buffer.append(" ");
    	if(property!=null) {
    		buffer.append(property);
    	}
    	buffer.append(">");
    	return buffer.toString();
    }

    /**
     * Parsea un tag con cierre con el nombre <code>tagName</code>,
     * y le agrega las properties pasadas en <code>properties</code>.
     * @param tagName nombre del tag
     * @param properties array de properties, cada una puede ser
     * parseada con {@link #prop(String, String)}
     * @return el tag XML parseado con los valores
     */
    public static String closedTag(String tagName, String ... properties) {
    	StringBuffer buffer = new StringBuffer(50);
    	buffer.append("<");
    	buffer.append(tagName);
    	buffer.append(" ");
    	for(int i=0; i<properties.length; i++) {
    		buffer.append(properties[i]);
    	}
    	buffer.append("/>");
    	return buffer.toString();
    }

    /**
     * Parsea un tag de cierre con el nombre <code>tagName</code>.
     */
    public static String endTag(String tagName) {
    	return "</" + tagName + ">";
    }

    /**
     * Crea un property de tag con el nombre <code>name</code>
     * y el valor <code>value</code>. Si <code>name</code>
     * o <code>value</code> son <code>null</code>, retorna
     * también <code>null</code>.
     */
    public static String prop(String name, String value) {
    	return name==null || value==null ?
    			null : name + "=\"" + value + "\"";
    }
}
