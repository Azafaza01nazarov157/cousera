package org.example.cursera.util;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.config.MinioConfig;
import org.example.cursera.config.props.MiniProp;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtil {

    private final MiniProp minioProperties;
    private final MinioClient minioClient;


    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());

            return true;
        } else {
            return false;
        }
    }


    @SneakyThrows
    public List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }


    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }



    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                //  There are object files , Delete failed
                if (item.size() > 0) {
                    return false;
                }
            }
            //  Delete buckets , Be careful , Only when the bucket is empty can it be deleted successfully .
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    public List<String> listObjectNames(String bucketName) {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        } else {
            listObjectNames.add(" Bucket does not exist ");
        }
        return listObjectNames;
    }


    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    @SneakyThrows
    public void putObject(String bucketName, MultipartFile multipartFile, String filename,
                          String fileType) {
        InputStream inputStream = new ByteArrayInputStream(multipartFile.getBytes());
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                                inputStream, -1, minioProperties.getFileSize())
                        .contentType(fileType)
                        .build());
    }



    @SneakyThrows
    public String getObjectUrl(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(2, TimeUnit.MINUTES)
                            .build());
            log.info("Generated signed URL: {}", url);
        }
        return url;
    }



    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        }
        return false;
    }



    public InputStream getObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            try {
                StatObjectResponse statObject = statObject(bucketName, objectName);
                if (statObject != null && statObject.size() > 0) {
                    return minioClient.getObject(
                            GetObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .build());
                }
            } catch (ErrorResponseException | InsufficientDataException | InternalException |
                     InvalidKeyException | InvalidResponseException | IOException |
                     NoSuchAlgorithmException | ServerException | XmlParserException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public StatObjectResponse statObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse stat = null;
            try {
                stat = minioClient.statObject(
                        StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            } catch (Exception e) {
                return null;
            }
            return stat;
        }
        return null;
    }


    @SneakyThrows
    public boolean removeObject(String bucketName, List<String> objectNames) {
        boolean flag = bucketExists(bucketName);
        boolean deletionSuccess = true; // Initialize a flag to true

        if (flag) {
            List<DeleteObject> objects = new LinkedList<>();
            for (int i = 0; i < objectNames.size(); i++) {
                objects.add(new DeleteObject(objectNames.get(i)));
            }
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error in deleting object {}: {}", error.objectName(), error.message());
                deletionSuccess = false; // Set the flag to false if there's an error
            }
        }

        return deletionSuccess; // Return the flag's value
    }


    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .offset(offset)
                                .length(length)
                                .build());
            }
        }
        return null;
    }


    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, InputStream inputStream,
                             String contentType) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    inputStream, -1, minioProperties.getFileSize())
                            .contentType(contentType)
                            .build());
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return true;
            }
        }
        return false;
    }

}