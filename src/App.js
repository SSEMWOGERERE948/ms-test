import React, { useEffect, useState } from 'react';
import LoginButton from './login';
import LogoutButton from './logout';
import { gapi } from 'gapi-script';
import WordDocUploader from './WordDocUploader';

function App() {
  const [fileId, setFileId] = useState(null);  // State to store the uploaded file's ID
  const [credentials, setCredentials] = useState({ clientId: '', apiKey: '', scopes: '' });

  useEffect(() => {
    // Fetch credentials from the backend
    fetch('/api/credentials')
      .then(response => response.json())
      .then(data => setCredentials(data))
      .catch(error => console.error('Error fetching credentials:', error));
  }, []);

  useEffect(() => {
    if (credentials.clientId && credentials.apiKey) {
      function start() {
        gapi.client.init({
          apiKey: credentials.apiKey,
          clientId: credentials.clientId,
          scope: credentials.scopes
        }).then(() => {
          console.log('Google API client initialized');
        }).catch(error => {
          console.error('Error initializing Google API client:', error);
        });
      }

      gapi.load('client:auth2', start);
    }
  }, [credentials]);

  function uploadFile(file) {
    const auth = gapi.auth2.getAuthInstance();
    const accessToken = auth.currentUser.get().getAuthResponse().access_token;

    if (!accessToken) {
      console.error('No access token found');
      return;
    }

    const metadata = {
      name: file.name, // Keep the original name of the file
      mimeType: file.type,
    };

    const formData = new FormData();
    formData.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }));
    formData.append('file', file);

    fetch('https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart', {
      method: 'POST',
      headers: new Headers({
        'Authorization': 'Bearer ' + accessToken,
      }),
      body: formData,
    })
    .then((res) => {
      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }
      return res.json();
    })
    .then((data) => {
      console.log('File uploaded successfully:', data);
      setFileId(data.id);  // Store the file ID after upload
    })
    .catch((error) => {
      console.error('Error uploading file:', error);
    });
  }

  function handleFileChange(event) {
    const file = event.target.files[0];
    if (file) {
      uploadFile(file); // Upload the selected file
    }
  }

  const fileUrl = fileId ? `https://docs.google.com/document/d/${fileId}/edit` : null;

  return (
    <div className="App">
      <LoginButton />
      <LogoutButton />
      <input type="file" accept=".doc, .docx" onChange={handleFileChange} /> {/* File input for Word documents */}
      <WordDocUploader />

      {fileUrl && (
        <iframe
          src={fileUrl}
          width="100%"
          height="600px"
          title="Google Docs Viewer"
        />
      )}
    </div>
  );
}

export default App;
