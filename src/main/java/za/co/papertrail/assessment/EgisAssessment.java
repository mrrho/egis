package za.co.papertrail.assessment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import za.co.papertrail.assessment.data.EgisTechnologies;

import java.io.IOException;

public class EgisAssessment {

    private String url;

    public EgisAssessment(String url) {
        this.url = url;
    }

    public static void main(String[] args) {
        try {
            EgisTechnologies technologies = new EgisAssessment("https://github.com/egis/handbook/blob/master/Tech-Stack.md").scrape();
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

    public EgisTechnologies scrape() throws IOException {
        Document document = Jsoup.connect(url).get();
        return new EgisTechnologies();
    }

}
