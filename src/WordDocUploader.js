import React, { useState } from 'react';
import { renderAsync } from 'docx-preview';

const WordDocUploader = () => {
  const [fileURL, setFileURL] = useState(null); // To store the URL of the uploaded file

  // Handle the file upload
  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (file) {
      const arrayBuffer = await file.arrayBuffer();
      
      // Create a temporary URL for the uploaded file to open in MS Word
      const blob = new Blob([arrayBuffer], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
      const url = URL.createObjectURL(blob);
      setFileURL(url);

      // Create an HTML container for the preview
      const container = document.getElementById('doc-preview');
      renderAsync(arrayBuffer, container, null, { inWrapper: false })
        .then(() => console.log('Document rendered successfully'))
        .catch((error) => console.error('Error rendering document:', error));
    }
  };

  // Function to open the document in MS Word
  const openInWord = () => {
    if (fileURL) {
      window.location.href = `ms-word:ofe|u|${fileURL}`;
    }
  };

  return (
    <div>
      <h2>Word Document Uploader and Viewer</h2>

      {/* File input for uploading Word document */}
      <input
        type="file"
        accept=".docx"
        onChange={handleFileUpload}
      />

      {/* Container where the Word document will be displayed */}
      <div id="doc-preview" style={{ border: '1px solid #ddd', padding: '10px', marginTop: '20px' }}>
        {/* The Word document will be rendered here */}
      </div>

      {/* Button to open the document in Microsoft Word for editing */}
      {fileURL && (
        <button onClick={openInWord} style={{ marginTop: '20px' }}>
          Edit in Word
        </button>
      )}
    </div>
  );
};

export default WordDocUploader;
