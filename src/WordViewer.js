// src/WordViewer.js

import React, { useState } from "react";
import DocViewer, { DocViewerRenderers } from "react-doc-viewer";

const WordViewer = () => {
  const [docs, setDocs] = useState([]);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const newDoc = { uri: URL.createObjectURL(file), fileName: file.name };
      setDocs([newDoc]); // Replace existing docs with the new file
    }
  };
  

  // Custom header override for the document viewer
  const myHeader = (state, previousDocument, nextDocument) => {
    if (!state.currentDocument || state.config?.header?.disableFileName) {
      return null;
    }

    return (
      <>
        <div>{state.currentDocument.uri || ""}</div>
        <div>
          <button
            onClick={previousDocument}
            disabled={state.currentFileNo === 0}
          >
            Previous Document
          </button>
          <button
            onClick={nextDocument}
            disabled={state.currentFileNo >= state.documents.length - 1}
          >
            Next Document
          </button>
        </div>
      </>
    );
  };

  return (
    <div className="WordViewer">
      <h2>Document Viewer</h2>

      {/* File input to select Word/PDF documents */}
      <input
        type="file"
        accept=".doc,.docx,.pdf"
        onChange={handleFileChange}
        style={{ marginBottom: "20px" }}
      />

      {/* Document Viewer */}
      {docs.length > 0 && (
        <DocViewer
          pluginRenderers={DocViewerRenderers}
          documents={docs}
          config={{
            header: {
              overrideComponent: myHeader,
            },
          }}
          style={{ width: "100%", height: "80vh" }} // Styling for the viewer
        />
      )}
    </div>
  );
};

export default WordViewer;
