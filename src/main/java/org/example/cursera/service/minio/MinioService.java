package org.example.cursera.service.minio;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface MinioService {
    /**
     * Saves an object to the specified bucket in MinIO.
     *
     * @param bucketName the name of the bucket
     * @param object     the file to save
     * @param objectName the name to give the object in MinIO
     * @return true if the object was saved successfully, false otherwise
     */
    Boolean saveObject(String bucketName, MultipartFile object, String objectName);

    /**
     * Retrieves a Resource representation of an object from MinIO.
     *
     * @param bucketName the name of the bucket
     * @param fileName   the name of the file to retrieve
     * @return a Resource representing the object
     */
    Resource getObjectResource(String bucketName, String fileName);

    /**
     * Retrieves an InputStream for an object from MinIO.
     *
     * @param bucketName the name of the bucket
     * @param fileName   the name of the file to retrieve
     * @return an InputStream for the object
     */
    InputStream getInputStreamFromMinio(String bucketName, String fileName);

    /**
     * Deletes an object from MinIO.
     *
     * @param bucketName the name of the bucket
     * @param objectName the name of the object to delete
     * @return true if the object was deleted successfully, false otherwise
     */
    Boolean deleteObject(String bucketName, String objectName);

    /**
     * Deletes multiple objects from MinIO.
     *
     * @param bucketName  the name of the bucket
     * @param objectNames the names of the objects to delete
     * @return true if the objects were deleted successfully, false otherwise
     */
    Boolean deleteMultipleObject(String bucketName, List<String> objectNames);
}
