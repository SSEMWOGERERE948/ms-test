package com.example.demo.controllers;

import com.example.demo.entities.DocumentEntity;
import com.example.demo.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            Long documentId = documentService.uploadDocument(file); // Save the document and return the ID

            Map<String, Object> response = new HashMap<>();
            response.put("documentId", documentId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to upload document"));
        }
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<String> getDocumentAsHtml(@PathVariable Long documentId) {
        try {
            String htmlContent = documentService.getDocumentAsHtml(documentId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/html")
                    .body(htmlContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Document not found");
        }
    }

    @PostMapping("/update/{documentId}")
    public ResponseEntity<Void> updateDocument(@PathVariable Long documentId, @RequestBody String newContent) {
        try {
            documentService.updateDocumentContent(documentId, newContent);
            return ResponseEntity.ok().build();  // Return OK if everything is successful
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Return 404 if document is not found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Handle other exceptions
        }
    }


    @GetMapping("/latest-id")
    public ResponseEntity<Long> getLatestDocumentId() {
        try {
            Long latestId = documentService.getLatestDocumentId();
            return ResponseEntity.ok(latestId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Endpoint for downloading a document
    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        return documentService.downloadWordDocument(documentId);
    }


    @GetMapping("/downloadWord/{documentId}")
    public ResponseEntity<ByteArrayResource> getDocumentAsWord(@PathVariable Long documentId) {
        try {
            byte[] wordDocument = documentService.getDocumentAsWord(documentId);
            if (wordDocument == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=document-" + documentId + ".docx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

            ByteArrayResource resource = new ByteArrayResource(wordDocument);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(wordDocument.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/credentials")
    public ResponseEntity<Map<String, String>> getCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("clientId", "245498184843-lrvct4acv7vg8508ldpd75rog7h89h2c.apps.googleusercontent.com");
        credentials.put("apiKey", "AIzaSyC9L4phalXWNuz5AzmcmEiWR2k502NndLg");
        credentials.put("scopes", "https://www.googleapis.com/auth/drive.file");

        return ResponseEntity.ok(credentials);
    }

    @PostMapping("/uploadScanned")
    public ResponseEntity<String> uploadScannedDocument(@RequestBody Map<String, String> request) {
        try {
            String base64Image = request.get("image");
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Save imageBytes to the database or filesystem
            // For example, if saving to filesystem:
            Path destinationFile = Paths.get("uploads/scanned_document.png");
            Files.write(destinationFile, imageBytes);

            return ResponseEntity.ok("Document uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload document.");
        }
    }

    @PostMapping("/start")
    public ResponseEntity<String> startScan() {
        try {
            // Path to the NAPS2 executable
            String executablePath = "executables/naps2.console.exe";  // Adjust based on your folder structure

            // Path to save the scanned PDFs (inside the project directory in src/main/resources/scanned-documents)
            String outputFolder = System.getProperty("user.dir") + File.separator + "src" + File.separator +
                    "main" + File.separator + "resources" + File.separator + "scanned-documents";
            String outputFile = "ScannedDocument.pdf";  // Name of the scanned file
            String outputPath = outputFolder + File.separator + outputFile;

            // Create the output folder if it doesn't exist
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdirs();  // Creates the folder if it doesn't exist
            }

            // Check if NAPS2 is installed or run the executable
            if (!isNaps2Installed()) {
                ProcessBuilder processBuilder = new ProcessBuilder(executablePath, "scan", "--profile", "Default", "--output", outputPath);
                processBuilder.redirectErrorStream(true);  // Redirect error stream to output stream

                // Start the process
                Process process = processBuilder.start();
                int exitCode = process.waitFor();  // Wait for the process to complete

                if (exitCode == 0) {
                    return ResponseEntity.ok("Scan completed and saved in project as " + outputPath);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Scan failed.");
                }
            } else {
                return ResponseEntity.ok("NAPS2 is already installed and ready for use.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during scanning: " + e.getMessage());
        }
    }

    @GetMapping("/document")
    public ResponseEntity<FileSystemResource> getScannedDocument() {
        try {
            // Path to the saved scanned document
            String outputFolder = System.getProperty("user.dir") + File.separator + "src" + File.separator +
                    "main" + File.separator + "resources" + File.separator + "scanned-documents";
            String outputPath = outputFolder + File.separator + "ScannedDocument.pdf";

            File file = new File(outputPath);

            if (file.exists()) {
                // Return the scanned PDF as a file download
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ScannedDocument.pdf");
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new FileSystemResource(file));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private boolean isNaps2Installed() {
        // Example: Check if NAPS2 is installed by verifying the existence of the executable
        File naps2Executable = new File("C:\\Program Files\\NAPS2\\naps2.console.exe");  // Adjust path as necessary
        return naps2Executable.exists();
    }
}
