package com.neueda.etiqet;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class AftermathEvidenceSink extends AppenderSkeleton {

    private static final String CLIENT_IN_MSG_KEY = "client-in";
    private static final String CLIENT_OUT_MSG_KEY = "client-out";
    private static final String CLIENT_LOGS_MSG_KEY = "client-logs";
    private static final String EXCHANGE_MSG_KEY = "exchange";

    private static final String CLIENT_MSG_DELIMITER_IN = "<<";
    private static final String CLIENT_MSG_DELIMITER_OUT = ">>";
    private static final String EXCHANGE_MSG_DELIMITER = "- Exchange message: ";

    private Map<String, List<Object>> evidence;
    private List<Object> clientIn;
    private List<Object> clientOut;
    private List<Object> exchange;
    private List<Object> clientLogs;
    private MemoryBuffer memoryBuffer;

    public AftermathEvidenceSink() {
        memoryBuffer = new MemoryBuffer(1);
        this.evidence = new LinkedHashMap<>();
        this.clientIn = new ArrayList<>();
        this.clientOut = new ArrayList<>();
        this.clientLogs = new ArrayList<>();
        this.exchange = new ArrayList<>();
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        try {
            String message = String.format("%s\n", loggingEvent.getMessage().toString());
            Files.write(Paths.get("C:/Users/Neueda/Documents/Workspace/etiqet/etiqet-core/src/main/java/com/neueda/etiqet/log.txt"), message.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
        processLogEvent(loggingEvent);
    }

    private void processLogEvent(LoggingEvent loggingEvent) {
        String message = String.format("%s\n", loggingEvent.getMessage().toString());
        if (message.startsWith(CLIENT_MSG_DELIMITER_IN)) {
            Map<String, String> renderedFixMessage =
                handleFixString(loggingEvent.getRenderedMessage().split(CLIENT_MSG_DELIMITER_IN)[1].trim());
            if (renderedFixMessage.size() > 0) {
                clientOut.add(renderedFixMessage);
            }
        }
        else if (message.startsWith(CLIENT_MSG_DELIMITER_OUT)) {
            Map<String, String> renderedFixMessage =
                handleFixString(loggingEvent.getRenderedMessage().split(CLIENT_MSG_DELIMITER_OUT)[1].trim());
            if (renderedFixMessage.size() > 0) {
                clientOut.add(renderedFixMessage);
            }
        }
        else if (message.startsWith("EXCHANGE_MSG_DELIMITER")) {
            handleExchange(loggingEvent);
        }
        else {
            clientLogs.add(loggingEvent.getMessage());
        }
        memoryBuffer.add(loggingEvent.getMessage());
    }

    private Map<String, String> handleFixString(String fixString) {
        String[] fixMessageParts = fixString.split("\u0001");
        Map<String, String> fixMessageMapped = new LinkedHashMap<>();
        for (String fixPart : fixMessageParts) {
            String[] fixTagValPairs = fixPart.split("=");
            if (fixTagValPairs.length < 2) {
                continue;
            }
            fixMessageMapped.put(fixTagValPairs[0], fixTagValPairs[1]);
        }
        return fixMessageMapped;
    }

    private void handleExchange(LoggingEvent loggingEvent) {
        // todo
    }

    Map<String, List<Object>> pollEvidence() {
        evidence.put(CLIENT_IN_MSG_KEY, clientIn);
        evidence.put(CLIENT_OUT_MSG_KEY, clientOut);
        evidence.put(EXCHANGE_MSG_KEY, exchange);
        evidence.put(CLIENT_LOGS_MSG_KEY, clientLogs);

        Map<String, List<Object>> evidenceCopy = new LinkedHashMap<>(evidence);
        // clear evidence to begin new test case
        evidence = new LinkedHashMap<>();
        return evidenceCopy;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    private static class MemoryBuffer extends AbstractQueue {

        private int maxSize;
        private List<Object> values;

        public MemoryBuffer(int maxSize) {
            this.maxSize = maxSize > 0 ? maxSize : 1;
            values = new ArrayList<>();
        }

        @Override
        public boolean add(Object o) {
            if (values.size() >= maxSize) {
                values.remove(values.size() - 1);
            }
            values.add(o);
            return true;
        }

        @Override
        public boolean addAll(Collection c) {
            boolean allAdded = true;
            for (Object item : c) {
                boolean thisOneAdded = add(item);
                if (allAdded && !thisOneAdded) {
                    allAdded = false;
                }
            }
            return allAdded;
        }

        @Override
        public Iterator iterator() {
            return null;
        }

        @Override
        public int size() {
            return values.size();
        }

        @Override
        public boolean offer(Object o) {
            if (values.size() >= maxSize) return false;
            values.add(o);
            return true;
        }

        @Override
        public Object poll() {
            return values.size() == 0 ? null : values.remove(values.size() - 1);
        }

        @Override
        public Object peek() {
            return values.size() == 0 ? null : values.get(values.size() - 1);
        }
    }
}
