import React, { useState, useEffect } from 'react';
import RemoteService from '../../remoteService';
import { useParams } from 'react-router-dom';
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import './DocumentEditor.css';

function DocumentEditor({ token }) {
  const { documentId } = useParams();
  const [content, setContent] = useState("");

  useEffect(() => {
    if (documentId) {
      fetchDocument();
    }
  }, [documentId]);

  const fetchDocument = async () => {
    try {
      RemoteService.fetchDocument(
        `documents/${documentId}`,
        (data) => {
          const text = new TextDecoder().decode(data);
          setContent(text);
        },
        (error) => {
          console.error('Error fetching document', error);
          alert('Error fetching document. Please try again.');
        }
      );
    } catch (error) {
      console.error('Error fetching document', error);
      alert('An error occurred. Please try again.');
    }
  };

  const saveDocument = async () => {
    try {
      await RemoteService.sendPostToServer(
        `/documents/update/${documentId}`,
        content,
        (response) => {
          alert('Document saved successfully!');
        },
        (error) => {
          if (error.response) {
            console.error('Error Response:', error.response);
            alert(`Error saving document. Server responded with status: ${error.response.status} - ${error.response.data?.message || 'Unknown error'}`);
          } else if (error.request) {
            console.error('No Response:', error.request);
            alert('Error saving document. No response received from the server. Please check your network connection.');
          } else {
            console.error('Error:', error.message);
            alert(`Error saving document: ${error.message}`);
          }
        }
      );
    } catch (error) {
      console.error('Unhandled Error:', error);
      alert('An unexpected error occurred. Please try again.');
    }
  };

  return (
    <div className="document-editor-container">
      <h2>Document Editor</h2>
      <CKEditor
        editor={ClassicEditor}
        config={{
          toolbar: {
            items: [
              'heading', '|', 'bold', 'italic', 'link', '|', 'bulletedList', 'numberedList', '|',
              'blockQuote', 'insertTable', 'mediaEmbed', '|', 'undo', 'redo'
            ],
            shouldNotGroupWhenFull: true
          },
          placeholder: 'Start typing your document here...'
        }}
        data={content}
        onChange={(event, editor) => {
          setContent(editor.getData());
        }}
      />
      <button onClick={saveDocument}>Save Document</button>
    </div>
  );
}

export default DocumentEditor;
