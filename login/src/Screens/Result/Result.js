// Result.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Result.css'; // Import the CSS file for styling

function Result({ token }) {
  const [jwtResults, setJwtResults] = useState([]);

  // Fetch student results when the token is available
  useEffect(() => {
    const fetchStudentResults = async () => {
      if (token) {
        try {
          const resultResponse = await axios.get('http://localhost:8080/alevel/results', {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          setJwtResults(resultResponse.data);
        } catch (error) {
          console.error('Error fetching student results', error);
        }
      }
    };
    fetchStudentResults();
  }, [token]);

  return (
    <div className="result-container">
      <h2>Student Results</h2>
      {jwtResults.length > 0 ? (
        <table className="results-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Candidate Name</th>
              <th>Candidate Number</th>
              <th>Year</th>
              <th>Random Code</th>
              <th>Subject Code</th>
            </tr>
          </thead>
          <tbody>
            {jwtResults.map((result) => (
              <tr key={result.id}>
                <td>{result.id}</td>
                <td>{result.candidateName}</td>
                <td>{result.candidateNumber}</td>
                <td>{result.year}</td>
                <td>{result.randomCode}</td>
                <td>{result.subjectCode}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No results available.</p>
      )}
    </div>
  );
}

export default Result;
