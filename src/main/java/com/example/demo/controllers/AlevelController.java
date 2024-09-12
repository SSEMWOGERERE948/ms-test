package com.example.demo.controllers;

import com.example.demo.entities.AlevelResult;
import com.example.demo.services.AlevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
