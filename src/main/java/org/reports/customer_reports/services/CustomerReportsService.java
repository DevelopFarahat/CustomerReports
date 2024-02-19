package org.reports.customer_reports.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.reports.customer_reports.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CustomerReportsService {

    @Autowired
    private ConsumerCustomerService consumerCustomerService;

    public List<ByteArrayInputStream> customerPageToCsvFile() {
        CompletableFuture<List<List<String[]>>> customerDataFutureList = consumerCustomerService.getCustomerPageData(Constants.pageSize);
        List<List<String[]>> customerDataRecords = customerDataFutureList.join();
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        List<ByteArrayInputStream> byteArrayInputStreamList = new CopyOnWriteArrayList<>();
        ExecutorService executorThread = Executors.newFixedThreadPool(8);
        List<CompletableFuture<Void>> completableFutureList = new ArrayList<>();

        for (List<String[]> records : customerDataRecords) {
            CompletableFuture<Void> recordsFuture = CompletableFuture.runAsync(() -> {
                try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
                    csvPrinter.printRecord(new String[]{"id","firstName","lastName","email","phone","gender","age"});
                    for (String[] record : records) {
                        csvPrinter.printRecord(record);
                    }
                    csvPrinter.flush();
                    byteArrayInputStreamList.add(new ByteArrayInputStream(out.toByteArray()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, executorThread);
            completableFutureList.add(recordsFuture);

        }

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[0]));
        allFuture.join();
        return byteArrayInputStreamList;
    }


    public ByteArrayResource generateZipFile() {
        List<ByteArrayInputStream> byteArrayInputStreamList = customerPageToCsvFile();

        // Create a ByteArrayOutputStream to hold the zip file contents
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(baos)) {
            for (int i = 0; i < byteArrayInputStreamList.size(); i++) {
                // Create a zip entry for each ByteArrayInputStream
                zipOutputStream.putNextEntry(new ZipEntry("customer_report_" + new Date() + " " + i + ".csv"));
                // Copy the data from the ByteArrayInputStream to the zip output stream
                byte[] buffer = new byte[1024];
                int len;
                while ((len = byteArrayInputStreamList.get(i).read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create zip file: " + e.getMessage());
        } finally {
            // Close ByteArrayInputStream instances here
            for (ByteArrayInputStream byteArrayInputStream : byteArrayInputStreamList) {
                try {
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    // Handle or log IOException if necessary
                }
            }
        }

        // Create a ByteArrayResource from the byte array containing the zip file data
        ByteArrayResource zipFileResource = new ByteArrayResource(baos.toByteArray());
        return zipFileResource;
    }


}
