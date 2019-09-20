package com.neueda.etiqet;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

public class AftermathService {

    private static final Logger LOG = LoggerFactory.getLogger(AftermathService.class);
    private LinkedList<CucumberFeatureRep> features;
    private CucumberFeatureRep currentFeature;

    private TestCase testCase;
    private String host;
    private final String ENTRY_POINT = "/api/buckets";
    private String projectName;
    private String testSuiteName;
    private String currentSection;

    public AftermathService(String host, String projectName, String testSuiteName) {
        this.host = host;
        this.projectName = projectName;
        this.testSuiteName = testSuiteName;
        this.features = new LinkedList<>();
    }

    private URL buildUrl() throws MalformedURLException, UnsupportedEncodingException {
        String path = host + ENTRY_POINT + "/" +
            URLEncoder.encode(projectName, "UTF-8").replace("+", "%20") + "/" +
            URLEncoder.encode(testSuiteName, "UTF-8").replace("+", "%20") + "/" +
            URLEncoder.encode(currentSection, "UTF-8").replace("+", "%20") + "/" +
            URLEncoder.encode(testCase.getName(), "UTF-8").replace("+", "%20");
        return new URL(path);
    }

    public void beginNewTestCase() {
        if (testCase != null) {
            setEndTimeStamp(new Date());
            emitTestCase();
        }
        testCase = new TestCase();
        setFeatureScenario();
        setBeginTimeStamp(new Date());
        // assume pass until failure
        setStatusId(true);
    }

    private void setFeatureScenario() {
        if (this.currentFeature == null || this.currentFeature.scenariosLeft() == 0) {
            this.currentFeature = features.poll();
            if (currentFeature != null) {
                this.currentSection = currentFeature.getFeatureName();
            }
        }
        if (this.currentFeature != null) {
            setName(this.currentFeature.pollScenario());
        }
    }

    private void emitTestCase() {
        LOG.info("Emitting test case to aftermath [{}]", testCase.getName());
        try {
            URL url = buildUrl();
            HttpURLConnection conn = createConnection(url);
            int responseCode = performPostRequest(conn, testCase.serializeAsJson().getBytes());
            LOG.info("Test case sent to aftermath Response: [{}]", responseCode);
        }
        catch (IOException e) {
            LOG.error("Failed to emit test case to aftermath [{}]", testCase.getName(), e);
        }
    }

    private HttpURLConnection createConnection(URL url) throws IOException {
        LOG.info("Connecting to aftermath at {}", url.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        return conn;
    }

    private int performPostRequest(HttpURLConnection conn, byte[] body) throws IOException {
        OutputStream os = conn.getOutputStream();
        os.write(body);
        os.flush();
        os.close();
        return conn.getResponseCode();
    }

    public void addFeature(String featureName, List<String> scenarios) {
        features.add(new CucumberFeatureRep(featureName, scenarios));
    }

    public void setName(String name) {
        testCase.name = name;
    }

    public void addComment(String comment) {
        testCase.comments.add(comment);
    }

    private void setElapsed(String elapsed) {
        testCase.elapsed = elapsed;
    }

    public void setStatusId(boolean isPass) {
        testCase.statusId = isPass ? TestCase.PASSED : TestCase.FAILED;
    }

    public void setEvidence(Map<String, List<Object>> evidence) {
        testCase.evidence = evidence;
    }

    private void setBeginTimeStamp(Date beginTimeStamp) {
        testCase.beginTimeStamp = beginTimeStamp;
    }

    private void setEndTimeStamp(Date endTimeStamp) {
        testCase.endTimeStamp = endTimeStamp;
        setElapsed(Long.toString((testCase.endTimeStamp.getTime() - testCase.beginTimeStamp.getTime())));
    }

    private static class CucumberFeatureRep {

        private String featureName;
        private LinkedList<String> scenarios;

        CucumberFeatureRep(String featureName, List<String> scenarios) {
            this.featureName = featureName;
            this.scenarios = new LinkedList<>(scenarios);
        }

        public String getFeatureName() {
            return featureName;
        }

        String pollScenario() {
            return scenarios.poll();
        }

        int scenariosLeft() {
            return scenarios.size();
        }
    }

    private static class TestCase {
        private static final String PASSED = "PASSED";
        private static final String FAILED = "FAILED";

        private String name;
        private List<String> comments;
        private String elapsed;
        private String statusId;
        private Map<String, List<Object>> evidence;
        private Date beginTimeStamp;
        private Date endTimeStamp;

        TestCase() {
            comments = new ArrayList<>();
            evidence = new LinkedHashMap<>();
        }

        private String serializeAsJson() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(this);
        }

        @JsonGetter("name")
        public String getName() {
            return name;
        }

        @JsonGetter("comment")
        public List<String> getComments() {
            return comments;
        }

        @JsonGetter("elapsed")
        public String getElapsed() {
            return elapsed;
        }

        @JsonGetter("status_id")
        public String getStatusId() {
            return statusId;
        }

        @JsonGetter("evidence")
        public Map<String, List<Object>> getEvidence() {
            return evidence;
        }

        @JsonGetter("begin_timestamp")
        public Date getBeginTimeStamp() {
            return beginTimeStamp;
        }

        @JsonGetter("end_timestamp")
        public Date getEndTimeStamp() {
            return endTimeStamp;
        }
    }
}
