package com.example.demo.controllers;

import com.example.demo.entities.DocumentEntity;
import com.example.demo.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

}
