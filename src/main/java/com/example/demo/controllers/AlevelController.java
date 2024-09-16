package com.example.demo.controllers;

import com.example.demo.entities.AlevelResult;
import com.example.demo.services.AlevelService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/alevel")
@CrossOrigin(origins = "*")
public class AlevelController {

    @Autowired
    private AlevelService alevelService;

    @GetMapping("/results")
    public List<AlevelResult> getAllResults() {
        return alevelService.getAllResults();
    }

    @GetMapping("/results/{candidateNumber}")
    public List<AlevelResult> getResultsByCandidateNumber(@PathVariable String candidateNumber) {
        return alevelService.getResultsByCandidateNumber(candidateNumber);
    }

    @GetMapping("/results/year/{year}")
    public List<AlevelResult> getResultsByYear(@PathVariable int year) {
        return alevelService.getResultsByYear(year);
    }

    @PostMapping("/create_result")
    public AlevelResult createResult(@RequestBody AlevelResult result) {
        return alevelService.createResult(result);
    }

    @PutMapping("/update_results/{id}")
    public ResponseEntity<String> updateResult(@PathVariable Long id, @RequestBody AlevelResult updatedResult) {
        try {
            alevelService.updateResult(id, updatedResult);
            return ResponseEntity.ok("Result updated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Result not found with id " + id);
        }
    }


    @DeleteMapping("/delete_result/{id}")
    public void deleteResult(@PathVariable Long id) {
        alevelService.deleteResult(id);
    }

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        // Process the file with Apache POI
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            // Example: Read content from the document
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                System.out.println(paragraph.getText());
            }
        }
        return "Document uploaded successfully";
    }

    @PostMapping("/edit")
    public byte[] editDocument(@RequestParam("file") MultipartFile file, @RequestParam("content") String content) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            // Example: Edit the document content
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText(content);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            return outputStream.toByteArray();
        }
    }


}
