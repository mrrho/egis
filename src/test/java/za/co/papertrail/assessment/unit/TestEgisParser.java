package za.co.papertrail.assessment.unit;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import za.co.papertrail.assessment.EgisAssessment;

import java.util.List;
import java.util.Map;

public class TestEgisParser {

    @Test
    public void shouldParseSimpleDocument() {
        Document document = new W3CDom().fromJsoup(Jsoup.parse("<html><head /><body /></html>"));
        Map<String, List<String>> technologies = EgisAssessment.parse(document);
        Assert.assertNotNull(technologies);
    }

}
