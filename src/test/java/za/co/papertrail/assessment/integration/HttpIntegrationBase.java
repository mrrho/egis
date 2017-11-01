package za.co.papertrail.assessment.integration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import za.co.papertrail.assessment.util.EgisNanoHttpd;

import java.io.IOException;

public class HttpIntegrationBase {

    protected static final EgisNanoHttpd httpd = new EgisNanoHttpd();

    @BeforeClass
    public static void beforeClass() throws IOException {
        httpd.start();
    }

    @AfterClass
    public static void afterClass() {
        httpd.stop();
    }

}
