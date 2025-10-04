package com.dev.bulk.generator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

@Slf4j
@Component
public class DatasetGenerator {

    private static final int ROWS = 1000000;
    private static final int COLUMNS = 40;
    private static final int BATCH_SIZE = 10000;

//    public static void main(String[] args) throws IOException {
//        DatasetGenerator datasetGenerator = new DatasetGenerator();
//        datasetGenerator.generate();
//    }

    public void generate() throws IOException{
        long startTime = System.currentTimeMillis();
        System.out.println("file generation starts");
        String filePath = "/home/guyvinay/dev/repo/dev-microservices-lab/dev-auth-server/src/main/resources/123456_ds_001.csv";

        try (
                FileWriter fileWriter = new FileWriter(filePath);
                CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
        ) {
            for(int col=1; col<=COLUMNS; col++) {
                csvPrinter.print("column_" + col);
            }
            csvPrinter.println();
            Random random = new Random();
            for (int row=1; row<=ROWS; row++) {
                for (int col=1; col<=COLUMNS; col++) {
                    csvPrinter.print("data_" + row + "_" + col + "_" + random.nextInt(1000));
                }
                csvPrinter.println();

                if (row % BATCH_SIZE == 0) {
                    log.info("{} flushed to file", BATCH_SIZE);
                    csvPrinter.flush(); // flush in csv after every 10k record.
                }
            }
            csvPrinter.flush();
        }
        log.info("CSV dataset generated at: {}", filePath);
        log.info("Executed in : {} ms", (System.currentTimeMillis() - startTime));
    }
}
