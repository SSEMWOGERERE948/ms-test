package com.example.demo.repositories;

import com.example.demo.entities.AlevelResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlevelRepo extends JpaRepository<AlevelResult, Long> {

    // Fetch all results
    @Query("SELECT a FROM AlevelResult a")
    List<AlevelResult> findAllResults();

    // Fetch results by candidate number
    @Query("SELECT a FROM AlevelResult a WHERE a.candidateNumber = :candidateNumber")
    List<AlevelResult> findByCandidateNumber(@Param("candidateNumber") String candidateNumber);

    // Fetch results by year
    @Query("SELECT a FROM AlevelResult a WHERE a.year = :year")
    List<AlevelResult> findByYear(@Param("year") int year);
}
