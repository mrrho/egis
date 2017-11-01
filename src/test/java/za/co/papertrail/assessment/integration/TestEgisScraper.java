package za.co.papertrail.assessment.integration;

import fi.iki.elonen.NanoHTTPD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import za.co.papertrail.assessment.EgisAssessment;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestEgisScraper extends HttpIntegrationBase {

    @Before
    public void before() {
        httpd.reset();
    }

    @Test
    public void shouldParseSimpleResponse() throws IOException {
        httpd.serve("<html><body><h1>Test</h1></body></html>").withStatus(NanoHTTPD.Response.Status.OK);
        Map<String, List<String>> technologies =
                EgisAssessment.scrape("http://localhost:" + httpd.getListeningPort());
        Assert.assertNotNull(technologies);
    }

    @Test
    public void shouldParseSuccessAndRedirectStatuses() throws IOException {
        for(NanoHTTPD.Response.Status status: NanoHTTPD.Response.Status.values()) {
            if(status.getRequestStatus() >= 400) {
                continue;
            }
            EgisAssessment.scrape("http://localhost:" + httpd.getListeningPort());
        }
    }

    @Test
    public void shouldHandleErrorStatusesElegantly() {
        for(NanoHTTPD.Response.Status status: NanoHTTPD.Response.Status.values()) {
            if(status.getRequestStatus() < 400) {
                continue;
            }
            try {
                httpd.serve("html />").withStatus(status);
                EgisAssessment.scrape("http://localhost:" + httpd.getListeningPort());
                // this is unexpected
                Assert.fail("Status " + status + " should have caused an exception");
            } catch (IOException ignore) {
                // this is expected
            }
        }
    }

}
