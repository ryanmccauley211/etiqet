package com.neueda.etiqet.core;

import com.neueda.etiqet.AftermathService;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class AftermathRunListener extends RunListener {

    private final AftermathService aftermathService;
    private String currentTestCase;
    private TestState testState;
    private String currentStep;

    AftermathRunListener(AftermathService aftermathService) {
        this.aftermathService = aftermathService;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        super.testStarted(description);
        String testCaseName = description.getDisplayName().split("Scenario: ")[1]
            .replaceAll("[)]", "");
        if (currentTestCase == null || !currentTestCase.equals(testCaseName)) {
            aftermathService.beginNewTestCase();
            currentTestCase = testCaseName;
        }
        // assume pass unless fail
        this.testState = TestState.PASSED;
        this.currentStep = description.getMethodName();
    }

    @Override
    public void testFinished(Description description) throws Exception {
        super.testFinished(description);
        aftermathService.addComment(String.format(">%-20s: %s", testState, currentStep));
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        super.testFailure(failure);
        this.testState = TestState.FAILED;
        aftermathService.setStatusId(false);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        super.testAssumptionFailure(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        super.testIgnored(description);
        testState = TestState.SKIPPED;
        aftermathService.addComment(String.format(">%-20s: %s", testState, description.getMethodName()));
    }

    private enum TestState {
        PASSED,
        FAILED,
        SKIPPED
    }
}
