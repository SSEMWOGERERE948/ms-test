import React, { useState } from 'react';
import RemoteService from '../../remoteService';
import { useNavigate } from 'react-router-dom';

function FileUpload({ token }) {
  const [file, setFile] = useState(null);
  const [uploadedFiles, setUploadedFiles] = useState([]); // State for uploaded files
  const [downloadLink, setDownloadLink] = useState(null); // State to hold download URL
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
      // Upload the file
      await RemoteService.sendMultiPartRequestToTheServer(
        '/documents/upload',
        formData,
        async (uploadResponse) => {
          console.log('File uploaded successfully:', uploadResponse);

          // Fetch latest document ID
          const latestIdResponse = await RemoteService.sendRequestToGetLatestDocumentId();
          console.log('Latest ID Response:', latestIdResponse);

          // Ensure the response structure is correct
          if (latestIdResponse && latestIdResponse.data) {
            const latestId = latestIdResponse.data;

            // Update the uploaded files list
            setUploadedFiles((prevFiles) => [...prevFiles, { name: file.name, id: latestId }]);

            // Generate download link
            const documentUrl = `http://localhost:8080/downloadWord/${latestId}`;
            setDownloadLink(documentUrl);
          } else {
            console.error('Document ID not found in response');
            alert('Error: Document ID not found in response.');
          }
        },
        (error) => {
          console.error('Error uploading file:', error);
          alert('Error uploading file. Please try again.');
        }
      );
    } catch (error) {
      console.error('Error uploading file:', error);
      alert('An error occurred. Please try again.');
    }
  };

  return (
    <div>
      <h2>Upload a Document</h2>
      <input type="file" onChange={handleFileChange} />
      <button onClick={handleUpload}>Upload File</button>

      {/* Display uploaded files with download links */}
      <h3>Uploaded Files:</h3>
      <ul>
        {uploadedFiles.map((file) => (
          <li key={file.id}>
            {file.name} (ID: {file.id}) - 
            <a
              href={`http://localhost:8080/downloadWord/${file.id}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              Download
            </a>
          </li>
        ))}
      </ul>

      {/* Display download link if available */}
      {downloadLink && (
        <div>
          <h3>Open Document:</h3>
          <a
            href={`ms-word:ofe|u|${downloadLink}`}
            target="_blank"
            rel="noopener noreferrer"
          >
            Open in Microsoft Word
          </a>
        </div>
      )}
    </div>
  );
}

export default FileUpload;
