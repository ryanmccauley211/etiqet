package com.neueda.etiqet;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            handleClientIn();
        }
        else if (message.startsWith(CLIENT_MSG_DELIMITER_OUT)) {
            handleClientOut();
        }
        else if (message.startsWith("EXCHANGE_MSG_DELIMITER")) {
            handleExchange(loggingEvent);
        }
        else {
            clientLogs.add(loggingEvent.getMessage());
        }
        memoryBuffer.add(loggingEvent.getMessage());
    }

    private void handleClientIn() {
        Object previousLog = memoryBuffer.poll();
        if (previousLog != null) {
            Pattern insideBracketsPattern = Pattern.compile("\\[(.+?)]", Pattern.DOTALL);
            List<String> keyValuePairs = new ArrayList<>();
            Matcher bracketMatcher = insideBracketsPattern.matcher(previousLog.toString());
            while (bracketMatcher.find()) {
                keyValuePairs.add(bracketMatcher.group(1));
            }

            Pattern keyValuePattern = Pattern.compile("key=(.+?), value=(.+?)", Pattern.DOTALL);
            Map<String, String> keyValueMap = new HashMap<>();
            Matcher keyValueMatcher = keyValuePattern.matcher(previousLog.toString());
            while (keyValueMatcher.find()) {
                keyValueMap.put(keyValueMatcher.group(1), keyValueMatcher.group(2));
            }
        }
    }

    private void handleClientOut() {
        // todo
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
