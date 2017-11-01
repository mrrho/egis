package za.co.papertrail.assessment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple scraper/parser for the online EGIS technology list.
 *
 * Provides two API methods for parsing the list in HTML. The scrape {@link EgisAssessment#scrape(String)} scrape}
 * method consumes a URL string and {@link EgisAssessment#parse(Document) parse} provides a method that consumes a
 * standard W3C {@link Document document}. Both return a {@link Map map} that lists the technologies
 * by category.
 */
public class EgisAssessment {

    private String url;

    /**
     * Default constructor for a technology list scraper/parser
     */
    public EgisAssessment() {
    }

    public static void main(String[] args) {
        try {
            Map<String, List<String>> technologies = new EgisAssessment().scrape(
                    "https://github.com/egis/handbook/blob/master/Tech-Stack.md");
            System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(technologies));
        }
        catch (JsonProcessingException e) {
            System.err.println("Error serializing technologies");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error reading from url");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Scrapes an http resource for a list of technologies
     * @param url String representation of the URL to scrape.
     * @return list of technolopgies grouped by category
     * @throws IOException when an error occurs reading from the resource
     */
    public static Map<String, List<String>> scrape(String url) throws IOException {
        // use org.w3c.Document so that other compliant tools can also provide a document for parsing
        Document document = new W3CDom().fromJsoup(Jsoup.connect(url).get());
        return parse(document);
    }

    /**
     * Parses a technology list from a standard {@link Document}
     * @param document Input {@link Document document} to parse
     * @return list of technologies grouped by category
     */
    public static Map<String, List<String>> parse(Document document) throws IOException {
//        XPath path = XPathFactory.newInstance().newXPath().evaluate(
//                "/html/body/div[@role='main']/div[@itemscope]/div[@data-pjax-container]/div[@container]" +
//                        "/div[@class='repository-content']/div[@class='file']")
        Map<String, List<String>> results = new HashMap<>();
        try {
            NodeList nodes = (NodeList) XPathFactory.newInstance().newXPath().evaluate(
                    "/html/body/div[@role='main']/div[@itemscope]/div[@data-pjax-container]" +
                            "/div[contains(@class, 'container')]/div[@class='repository-content']/div[@class='file']" +
                            "/div[@id='readme']/article/*",
                    document.getDocumentElement(), XPathConstants.NODESET);
            int index = 0;
            while(index < nodes.getLength()) {
                index = processNodes(nodes, index, results);
            }
        } catch (XPathExpressionException ignore) {
            // the xpath expression provided is internal and should always be correct
        }
        return results;
    }

    private static int processNodes(NodeList nodes, int index, Map<String, List<String>> results) throws IOException {
        // skip any nodes that isn't the following h2 node
        while(index < nodes.getLength() && (nodes.item(index).getNodeType() != Node.ELEMENT_NODE ||
                !"h2".equals(((Element)nodes.item(index)).getTagName()))) {
            ++index;
        }
        if(index == nodes.getLength()) {
            return nodes.getLength();
        }
        String name = ((Element)nodes.item(index++)).getTextContent();
        List<String> techNames = results.get(name);
        if(techNames == null) {
            techNames = new ArrayList<>();
            results.put(name, techNames);
        }
        // skip any nodes that isn't the following table node
        while(index < nodes.getLength() && (nodes.item(index).getNodeType() != Node.ELEMENT_NODE ||
                !"table".equals(((Element)nodes.item(index)).getTagName()))) {
            ++index;
        }
        if(index == nodes.getLength()) {
            throw new IOException("Expected table after heading for technologies");
        }
        Element tableNode = (Element) nodes.item(index++);
        try {
            NodeList techCells = (NodeList) XPathFactory.newInstance().newXPath().evaluate(
                    "tbody/tr/td[1]", tableNode, XPathConstants.NODESET);
            for(int i = 0; i < techCells.getLength(); ++i) {
                techNames.add(techCells.item(i).getTextContent());
            }
        } catch (XPathExpressionException ignore) {
            // the xpath expression should always be correct
        }
        return index;
    }

}
