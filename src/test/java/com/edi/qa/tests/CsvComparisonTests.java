package com.edi.qa.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.edi.qa.core.CsvComparator;
import com.edi.qa.model.ComparisonResult;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;

public class CsvComparisonTests {

    private ExtentReports extent;
    private ExtentTest test;

    @BeforeSuite
    public void setup() {
        ExtentSparkReporter spark = new ExtentSparkReporter("test-output/ExtentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("JDK Version", System.getProperty("java.version"));
    }

    @Test(description = "Compare two identical CSV files")
    public void testIdenticalCsvFiles() throws Exception {
        test = extent.createTest("Identical CSV Files Test");

        File file1 = new File("src/test/resources/testdata/expected.csv");
        File file2 = new File("src/test/resources/testdata/actual.csv");

        test.info("Comparing: " + file1.getName() + " vs " + file2.getName());

        ComparisonResult result = CsvComparator.compare(file1, file2, "Identical-Files-Test");

        test.info("Time taken: " + result.getComparisonTimeMs() + "ms");

        if (result.isMatch()) {
            test.log(Status.PASS, result.getSummary());
        } else {
            test.log(Status.FAIL, result.getSummary());
            for (ComparisonResult.Difference diff : result.getDifferences()) {
                test.fail(diff.toString());
            }
        }

        Assert.assertTrue(result.isMatch(), "Files should be identical");
    }

    @Test(description = "Compare two different CSV files - expect differences")
    public void testDifferentCsvFiles() throws Exception {
        test = extent.createTest("Different CSV Files Test");

        File file1 = new File("src/test/resources/testdata/expected.csv");
        File file2 = new File("src/test/resources/testdata/different.csv");

        test.info("Comparing: " + file1.getName() + " vs " + file2.getName());

        ComparisonResult result = CsvComparator.compare(file1, file2, "Different-Files-Test");

        if (!result.isMatch()) {
            test.log(Status.PASS, "✅ Correctly detected " + result.getDifferences().size() + " difference(s)");
            for (ComparisonResult.Difference diff : result.getDifferences()) {
                test.warning(diff.toString());
            }
        } else {
            test.fail("Should have detected differences but didn't");
        }

        Assert.assertFalse(result.isMatch(), "Should detect differences");
    }

    @AfterSuite
    public void tearDown() {
        extent.flush();
        System.out.println("📊 Report generated at: test-output/ExtentReport.html");
    }
}
