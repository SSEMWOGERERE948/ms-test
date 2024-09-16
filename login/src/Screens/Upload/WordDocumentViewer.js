import React, { useState, useEffect } from 'react';
import axios from 'axios';

const WordDocumentViewer = ({ documentId }) => {
  const [documentUrl, setDocumentUrl] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchWordDocument = async () => {
      try {
        const response = await axios({
          url: `http://localhost:8080/downloadWord/${documentId}`, // Your Spring Boot URL
          method: 'GET',
          responseType: 'blob', // Important for fetching binary data
        });

        // Create a URL for the fetched file
        const file = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
        const fileUrl = URL.createObjectURL(file);
        setDocumentUrl(fileUrl);
        setLoading(false);
      } catch (error) {
        setError('Failed to fetch the document');
        setLoading(false);
      }
    };

    fetchWordDocument();
  }, [documentId]);

  if (loading) return <div>Loading document...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      {documentUrl && (
        <iframe
          title="Word Document Viewer"
          width="100%"
          height="600"
          src={`https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(documentUrl)}`}
          frameBorder="0"
        >
          Your browser does not support viewing this document.
        </iframe>
      )}
    </div>
  );
};

export default WordDocumentViewer;
