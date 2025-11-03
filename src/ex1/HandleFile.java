package ex1;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class HandleFile {
    private String fileName;
    private int blockSize = 64;

    public HandleFile(String fileName) {
        this.fileName = fileName;
    }

    public HandleFile(String fileName, int blockSize) {
        this.fileName = fileName;
        this.blockSize = blockSize;
    }

    public String readOneBlock() throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] buffer = new byte[blockSize];
            int bytesRead = bis.read(buffer); // read one block only

            if (bytesRead == -1) {
                return ""; // EOF or empty file
            }

            return new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
        }
    }

    /**
     * Reads the file in fixed-size byte chunks, but only returns complete CSV records (lines).
     */
    public List<Record> readFileByChunks() throws IOException {
        List<Record> records = new ArrayList<>();

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] buffer = new byte[blockSize];
            int bytesRead;
            StringBuilder leftover = new StringBuilder();  // stores incomplete line from previous chunk
            int blockCount = 0;

            while ((bytesRead = bis.read(buffer)) != -1) {
                blockCount++;

                // Convert bytes to string
                String chunk = new String(buffer, 0, bytesRead);

                // Add leftover from previous block
                chunk = leftover + chunk;

                // Split by newline
                String[] lines = chunk.split("\\r?\\n");

                // If the last line is incomplete, save it for next chunk
                leftover.setLength(0);
                if (!chunk.endsWith("\n") && !chunk.endsWith("\r")) {
                    leftover.append(lines[lines.length - 1]);
                    // Don’t process last partial line
                    lines = java.util.Arrays.copyOf(lines, lines.length - 1);
                }

                // Process full lines only
                for (String line : lines) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        try {
                            records.add(new Record(line));
                        } catch (Exception e) {
                            System.err.println("Skipping invalid record: " + line);
                        }
                    }
                }
            }

            // Process last leftover only if it’s complete

        }

        return records;
    }
}
