package com.edi.qa.model;
import java.util.ArrayList;
import java.util.List;

public class ComparisonResult {

    private String testCaseName;
    private String file1;
    private String file2;
    private boolean isMatch;
    private List<Difference> differences = new ArrayList<>();
    private long comparisonTimeMs;

    public ComparisonResult(String testCaseName, String file1, String file2) {
        this.testCaseName = testCaseName;
        this.file1 = file1;
        this.file2 = file2;
        this.isMatch = true;
    }

    public void addDifference(String field, Object expected, Object actual) {
        differences.add(new Difference(field, expected, actual));
        isMatch = false;
    }

    public boolean isMatch() { return isMatch; }
    public List<Difference> getDifferences() { return differences; }
    public void setComparisonTimeMs(long time) { this.comparisonTimeMs = time; }
    public long getComparisonTimeMs() { return comparisonTimeMs; }
    public String getTestCaseName() { return testCaseName; }

    public static class Difference {
        private String field;
        private Object expected;
        private Object actual;

        public Difference(String field, Object expected, Object actual) {
            this.field = field;
            this.expected = expected;
            this.actual = actual;
        }

        public String getField() { return field; }
        public Object getExpected() { return expected; }
        public Object getActual() { return actual; }

        @Override
        public String toString() {
            return String.format("[%s] Expected: '%s' | Actual: '%s'", field, expected, actual);
        }
    }

    public String getSummary() {
        if (isMatch) {
            return "✅ PASS - Files match completely";
        } else {
            return "❌ FAIL - Found " + differences.size() + " difference(s)";
        }
    }
}
