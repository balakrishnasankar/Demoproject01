package com.edi.qa.core;

import com.edi.qa.model.ComparisonResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CsvComparator {
    public static ComparisonResult compare(File file1, File file2, String testCaseName) throws Exception {
        long startTime = System.currentTimeMillis();

        List<CSVRecord> records1 = readCsv(file1);
        List<CSVRecord> records2 = readCsv(file2);

        ComparisonResult result = new ComparisonResult(testCaseName, file1.getName(), file2.getName());

        // Compare row count
        if (records1.size() != records2.size()) {
            result.addDifference("Row Count", records1.size(), records2.size());
        }

        // Compare each row
        int minRows = Math.min(records1.size(), records2.size());
        for (int i = 0; i < minRows; i++) {
            compareRecords(records1.get(i), records2.get(i), i, result);
        }

        result.setComparisonTimeMs(System.currentTimeMillis() - startTime);
        return result;
    }

    private static List<CSVRecord> readCsv(File file) throws IOException {
        Reader reader = Files.newBufferedReader(file.toPath());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withTrim()
                .parse(reader);

        List<CSVRecord> list = new ArrayList<>();
        for (CSVRecord record : records) {
            list.add(record);
        }
        return list;
    }

    private static void compareRecords(CSVRecord record1, CSVRecord record2, int rowNum, ComparisonResult result) {
        Map<String, String> map1 = record1.toMap();
        Map<String, String> map2 = record2.toMap();

        for (String header : map1.keySet()) {
            String value1 = map1.get(header);
            String value2 = map2.get(header);

            if (!Objects.equals(value1, value2)) {
                result.addDifference("Row " + rowNum + " - " + header, value1, value2);
            }
        }
    }
}
