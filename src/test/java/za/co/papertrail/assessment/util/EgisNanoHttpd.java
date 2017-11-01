package za.co.papertrail.assessment.util;

import fi.iki.elonen.NanoHTTPD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class EgisNanoHttpd extends NanoHTTPD {

    private static final int DEFAULT_TEST_PORT = 10099;
    private static int port;

    static {
        if(System.getProperty("test.port") == null) {
            port = DEFAULT_TEST_PORT;
        }
        else {
            port = Integer.parseInt(System.getProperty("test.port"));
        }
    }

    private String serveText;
    private Response.IStatus serverStatus;
    private String mimeType;
    private boolean compressed;

    public EgisNanoHttpd() {
        super("localhost", port);
    }

    public EgisNanoHttpd withStatus(Response.IStatus code) {
        this.serverStatus = code;
        return this;
    }

    public EgisNanoHttpd serve(String text) {
        this.serveText = text;
        return this;
    }

    public EgisNanoHttpd withMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public EgisNanoHttpd compressed() {
        this.compressed = true;
        return this;
    }

    public void reset() {
        this.serveText = null;
        this.serverStatus = null;
        this.mimeType = null;
        this.compressed = false;
    }

    @Override
    public Response serve(IHTTPSession session) {
        if(compressed) {
            ByteArrayOutputStream baos;
            try (GZIPOutputStream gzos = new GZIPOutputStream(baos = new ByteArrayOutputStream())) {
                gzos.write(this.serveText.getBytes());
                this.serveText = baos.toString();
            } catch (IOException e) {
                this.serverStatus = Response.Status.INTERNAL_ERROR;
                this.serveText = "An error occured [" + e.getLocalizedMessage() + "].";
                this.mimeType = "text/plain";
            }
        }
        return newFixedLengthResponse(this.serverStatus != null ? this.serverStatus : Response.Status.OK,
                this.mimeType != null ? this.mimeType : "text/html; charset=utf-8",
                this.serveText);
    }

}
