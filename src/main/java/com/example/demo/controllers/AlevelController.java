package com.example.demo.controllers;

import com.example.demo.entities.AlevelResult;
import com.example.demo.services.AlevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alevel")
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
    public AlevelResult updateResult(@PathVariable Long id, @RequestBody AlevelResult updatedResult) {
        return alevelService.updateResult(id, updatedResult);
    }

    @DeleteMapping("/delete_result/{id}")
    public void deleteResult(@PathVariable Long id) {
        alevelService.deleteResult(id);
    }
}
