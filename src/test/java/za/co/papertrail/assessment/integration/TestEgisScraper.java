package za.co.papertrail.assessment.integration;

import fi.iki.elonen.NanoHTTPD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import za.co.papertrail.assessment.EgisAssessment;

import java.io.IOException;

public class TestEgisScraper extends HttpIntegrationBase {

    @Before
    public void before() {
        httpd.reset();
    }

    @Test
    public void shouldParseSimpleResponse() throws IOException {
        httpd.serve("<html><body><h1>Test</h1></body></html>").withStatus(NanoHTTPD.Response.Status.OK);
        EgisAssessment.EgisTechnologies technologies =
                new EgisAssessment().scrape("http://localhost:" + httpd.getListeningPort());
        Assert.assertNotNull(technologies);

    }

}
