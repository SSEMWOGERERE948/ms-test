package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.DocumentEntity;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    // Query to insert a document into the database
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO documents (name, content_type, file_data) VALUES (:name, :contentType, :fileData)", nativeQuery = true)
    void saveDocument(
            @Param("name") String name,
            @Param("contentType") String contentType,
            @Param("fileData") byte[] fileData
    );

    // Query to fetch a document by ID
    @Query(value = "SELECT file_data FROM documents WHERE id = :id", nativeQuery = true)
    byte[] findDocumentById(@Param("id") Long id);

    @Query(value = "SELECT file_data FROM documents WHERE id = :id", nativeQuery = true)
    byte[] findAllDocuments(@Param("id") Long id);


    // Query to update document content
    @Transactional
    @Modifying
    @Query(value = "UPDATE documents SET file_data = :fileData WHERE id = :documentId", nativeQuery = true)
    void updateDocumentContent(@Param("documentId") Long documentId, @Param("fileData") byte[] fileData);

    // Query to get the latest document ID
    @Query(value = "SELECT MAX(id) FROM documents", nativeQuery = true)
    Long findLatestDocumentId();

    // Query to fetch a document by ID
    @Query(value = "SELECT * FROM documents WHERE id = :id", nativeQuery = true)
    DocumentEntity downloadDocumentById(@Param("id") Long id);
}
