package com.example.demo.services;

import com.example.demo.entities.AlevelResult;
import com.example.demo.repositories.AlevelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Service
@CrossOrigin(origins = "http://localhost:3000")
public class AlevelService {

    @Autowired
    private AlevelRepo alevelRepo;

    public List<AlevelResult> getAllResults() {
        return alevelRepo.findAllResults();
    }

    public List<AlevelResult> getResultsByCandidateNumber(String candidateNumber) {
        return alevelRepo.findByCandidateNumber(candidateNumber);
    }

    public List<AlevelResult> getResultsByYear(int year) {
        return alevelRepo.findByYear(year);
    }

    public AlevelResult createResult(AlevelResult result) {
        return alevelRepo.save(result);
    }

    public AlevelResult updateResult(Long id, AlevelResult updatedResult) {
        Optional<AlevelResult> existingResult = alevelRepo.findById(id);
        if (existingResult.isPresent()) {
            AlevelResult result = existingResult.get();
            result.setCandidateName(updatedResult.getCandidateName());
            result.setCandidateNumber(updatedResult.getCandidateNumber());
            result.setYear(updatedResult.getYear());
            result.setRandomCode(updatedResult.getRandomCode());
            result.setSubjectCode(updatedResult.getSubjectCode());
            return alevelRepo.save(result);
        } else {
            throw new RuntimeException("Result not found with id " + id);
        }
    }

    public void deleteResult(Long id) {
        alevelRepo.deleteById(id);
    }
}
