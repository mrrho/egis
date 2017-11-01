package za.co.papertrail.assessment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * A simple scraper/parser for the online EGIS technology list.
 *
 * Provides two API methods for parsing the list in HTML. The scrape {@link EgisAssessment#scrape(String)} scrape}
 * method consumes a URL string and {@link EgisAssessment#parse(Document) parse} provides a method that consumes a
 * standard W3C {@link Document document}. Both return an {@link EgisTechnologies} structure that lists the technologies
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
            EgisTechnologies technologies = new EgisAssessment().scrape(
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
    public static EgisTechnologies scrape(String url) throws IOException {
        // use org.w3c.Document so that other compliant tools can also provide a document for parsing
        Document document = new W3CDom().fromJsoup(Jsoup.connect(url).get());
        return parse(document);
    }

    /**
     * Parses a technology list from a standard {@link Document}
     * @param document Input {@link Document document} to parse
     * @return list of technologies grouped by category
     */
    public static EgisTechnologies parse(Document document) {
        return new EgisTechnologies();
    }

    public static class EgisTechnologies {

    }

}
