package za.co.papertrail.assessment.unit;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import za.co.papertrail.assessment.EgisAssessment;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestEgisParser {

    @Test
    public void shouldParseSimpleDocument() throws IOException {
        Document document = new W3CDom().fromJsoup(Jsoup.parse("<html><head /><body /></html>"));
        Map<String, List<String>> technologies = EgisAssessment.parse(document);
        Assert.assertNotNull(technologies);
    }

    @Test
    public void shouldParseSingleCategorySingleTechnology() throws IOException {
        Document document = new W3CDom().fromJsoup(Jsoup.parse("<!DOCTYPE html><html lang=\"en\">" +
                "<meta charset=\"utf-8\"><link rel=\"dns-prefetch\" href=\"https://assets-cdn.github.com\"></head>" +
                "<body><div role=\"main\"><div itemscope><div data-pjax-container><div class=\"container\">" +
                "<div class=\"repository-content\"><div class=\"file\"><div id=\"readme\">" +
                "<article class=\"markdown-body\"><h2>Programming Stack</h2><table><tbody><tr><td>Java 8 / JVM</td>" +
                "<td></td><td></td></tr></tbody></table></article></div></div></div></div></div></div></body></html>"));
        Map<String, List<String>> technologies = EgisAssessment.parse(document);
        Assert.assertNotNull(technologies);
        Assert.assertEquals(1, technologies.size());
        Assert.assertTrue(technologies.containsKey("Programming Stack"));
        List<String> names = technologies.get("Programming Stack");
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("Java 8 / JVM", names.get(0));
    }

    @Test
    public void shouldParseSingleCategorySingleTechnologyWithInterlacedGarbage() throws IOException {
        Document document = new W3CDom().fromJsoup(Jsoup.parse("<!DOCTYPE html><html lang=\"en\">" +
                "<meta charset=\"utf-8\"><link rel=\"dns-prefetch\" href=\"https://assets-cdn.github.com\"></head>" +
                "<body><div role=\"main\"><div itemscope><div data-pjax-container><div class=\"container\">" +
                "<div class=\"repository-content\"><div class=\"file\"><div id=\"readme\">" +
                "<article class=\"markdown-body\">kjsdfkdjf<h2>Programming Stack</h2><p>something else</p><table><tbody><tr><td>Java 8 / JVM</td>" +
                "<td></td><td></td></tr></tbody></table><p>sjdhjsdhf</p>kjhsdfjhdfjdf</article></div></div></div></div></div></div></body></html>"));
        Map<String, List<String>> technologies = EgisAssessment.parse(document);
        Assert.assertNotNull(technologies);
        Assert.assertEquals(1, technologies.size());
        Assert.assertTrue(technologies.containsKey("Programming Stack"));
        List<String> names = technologies.get("Programming Stack");
        Assert.assertEquals(1, names.size());
        Assert.assertEquals("Java 8 / JVM", names.get(0));
    }

    @Test
    public void shouldNotParseCategoryInWrongElement() throws IOException {
        Document document = new W3CDom().fromJsoup(Jsoup.parse("<!DOCTYPE html><html lang=\"en\">" +
                "<meta charset=\"utf-8\"><link rel=\"dns-prefetch\" href=\"https://assets-cdn.github.com\"></head>" +
                "<body><div role=\"main\"><div class=\"container\">" +
                "<div class=\"repository-content\"><div class=\"file\"><div id=\"readme\">" +
                "<article class=\"markdown-body\"><h2>Programming Stack</h2><table><tbody><tr><td>Java 8 / JVM</td>" +
                "<td></td><td></td></tr></tbody></table></article></div></div></div></div></div></div></body></html>"));
        Map<String, List<String>> technologies = EgisAssessment.parse(document);
        Assert.assertNotNull(technologies);
        Assert.assertEquals(0, technologies.size());
    }

    @Test
    public void shouldParseMultipleCategoriesVariousTechnologies() throws IOException {
        Document document = new W3CDom().fromJsoup(Jsoup.parse("<!DOCTYPE html><html lang=\"en\">" +
                "<meta charset=\"utf-8\"><link rel=\"dns-prefetch\" href=\"https://assets-cdn.github.com\"></head>" +
                "<body><div role=\"main\"><div itemscope><div data-pjax-container><div class=\"container\">" +
                "<div class=\"repository-content\"><div class=\"file\"><div id=\"readme\">" +
                "<article class=\"markdown-body\"><h2>Programming Stack</h2><table><tbody><tr><td>Java 8 / JVM</td>" +
                "<td></td><td></td></tr><tr><td>Groovy</td>" +
                "<td>Developer friendly especially for scripting, XML and JSON</td><td></td></tr></tbody></table>" +
                "<h2><strong>Build Stack</strong></h2><table><thead><tr><th>Blah</th><th></th><th></th></tr></thead><tbody>" +
                "<tr><td>Gradle</td><td></td></tr><tr><td>Ant</td></tr>" +
                "<tr><td>Babel / Gulp</td><td></td><td></td><td></td></tr></tbody></table>" +
                "</article></div></div></div></div></div></div></body></html>"));
        Map<String, List<String>> technologies = EgisAssessment.parse(document);
        Assert.assertNotNull(technologies);
        Assert.assertEquals(2, technologies.size());
        Assert.assertTrue(technologies.containsKey("Programming Stack"));
        List<String> names = technologies.get("Programming Stack");
        Assert.assertEquals(2, names.size());
        Assert.assertEquals("Java 8 / JVM", names.get(0));
        Assert.assertEquals("Groovy", names.get(1));
        Assert.assertTrue(technologies.containsKey("Build Stack"));
        names = technologies.get("Build Stack");
        Assert.assertEquals(3, names.size());
        Assert.assertEquals("Gradle", names.get(0));
        Assert.assertEquals("Ant", names.get(1));
        Assert.assertEquals("Babel / Gulp", names.get(2));
    }


}
