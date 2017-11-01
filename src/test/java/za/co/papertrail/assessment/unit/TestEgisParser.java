package za.co.papertrail.assessment.unit;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import za.co.papertrail.assessment.EgisAssessment;

public class TestEgisParser {

    @Test
    public void shouldParseSimpleDocument() {
        Document document = new W3CDom().fromJsoup(Jsoup.parse("<html><head /><body /></html>"));
        EgisAssessment.EgisTechnologies technologies = new EgisAssessment().parse(document);
        Assert.assertNotNull(technologies);
    }

}
