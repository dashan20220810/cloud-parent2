package com.baisha.fileuploaddownload.task;

import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * @author yihui
 */
@Slf4j
@Component
public class DeleteTask {

    @Autowired
    private MinioClient minioClient;


    @Scheduled(cron = "0 0 6 * * *")
    //@Scheduled(cron = "0 0/1 * * * *")
    public void deleteMinio() {
        String telegramBucket = "telegram";
        log.info("开始删除 {} minio文件开始", telegramBucket);
        deleteMinio(telegramBucket);
        log.info("开始删除 {} minio文件结束", telegramBucket);
    }


    public void deleteMinio(String bucket) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Iterable<Result<Item>> list = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).build());
        list.forEach(result -> {
            Item item;
            try {
                item = result.get();
                String objectName = item.objectName();
                ZonedDateTime zonedDateTime = item.lastModified();
                String zonedDate = df.format(zonedDateTime);
                Date date = sdf.parse(zonedDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 30);
                Date objectNameDate = calendar.getTime();
                Date nowDate = new Date();
                if (nowDate.after(objectNameDate)) {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
                    log.info("删除{}桶中的文件{}", bucket, objectName);
                }
            } catch (ErrorResponseException errorResponseException) {
                errorResponseException.printStackTrace();
            } catch (InsufficientDataException insufficientDataException) {
                insufficientDataException.printStackTrace();
            } catch (InternalException internalException) {
                internalException.printStackTrace();
            } catch (InvalidKeyException invalidKeyException) {
                invalidKeyException.printStackTrace();
            } catch (InvalidResponseException invalidResponseException) {
                invalidResponseException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            } catch (ServerException serverException) {
                serverException.printStackTrace();
            } catch (XmlParserException xmlParserException) {
                xmlParserException.printStackTrace();
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        });


    }

}
