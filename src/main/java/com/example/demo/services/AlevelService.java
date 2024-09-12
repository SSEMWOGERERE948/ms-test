package com.example.demo.services;

import com.example.demo.entities.AlevelResult;
import com.example.demo.repositories.AlevelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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

    public void updateResult(Long id, AlevelResult updatedResult) {
        int updatedRows = alevelRepo.updateResult(
                id,
                updatedResult.getCandidateName(),
                updatedResult.getCandidateNumber(),
                updatedResult.getYear(),
                updatedResult.getRandomCode(),
                updatedResult.getSubjectCode()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("Result not found with id " + id);
        }
    }


    public void deleteResult(Long id) {
        alevelRepo.deleteById(id);
    }
}
