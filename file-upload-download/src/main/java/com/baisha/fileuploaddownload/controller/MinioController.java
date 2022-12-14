package com.baisha.fileuploaddownload.controller;

import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.IpUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author sys
 */
@RestController
@RequestMapping("minio")
@Api(tags = "文件(图片)上传与下载")
@Slf4j
public class MinioController {

    @Autowired
    private MinioClient client;

    @Value("${minio.secretKey}")
    private String configSecretKey;
    @Value("${minio.domain}")
    private String domain;

    private MinioClient getInstance() {
        return client;
    }

    //endpoint: 域名或IP  ，bucket: 不同项目不同名称
    @ApiOperation("文件（图片）上传,成功返回文件地址")
    @ApiImplicitParams({@ApiImplicitParam(name = "bigFileSecret", type = "String", value = "大文件(2M以上)需要秘钥。找管理员获取",
            dataTypeClass = String.class), @ApiImplicitParam(name = "bucket", type = "String", value = "bucket" +
            "名称,提前联系管理员获取", required = true, dataTypeClass = String.class),})
    @PostMapping("upload/{bucket}")
    public ResponseEntity<Map<String, Object>> upload(@PathVariable("bucket") String bucket,
                                                      @RequestPart("file") MultipartFile file, String bigFileSecret) {
        if (file == null) {
            return ResponseUtil.custom("文件不能为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseUtil.custom("不支持，请联系技术");
            }
            InputStream inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");

            if (CommonUtil.checkNull(bigFileSecret) || !checkBigFileSecret(bigFileSecret)) {
                if (size == 0 || size > 2 * 1024 * 1024) {
                    return ResponseUtil.custom("文件不能大于2M");
                }
            }

            String oriFilename = file.getOriginalFilename();
            String[] split = oriFilename.split("\\.");
            String suffix = split[split.length - 1];
            String filename = UUID.randomUUID().toString() + "." + suffix;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    -1).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = domain + "/" + bucket + "/" + filename;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("url", url);
            data.put("fileKey", fileKey);
            return ResponseUtil.success(data);
            //http://10.0.2.15:9000/baisha/16cc42a1-84ba-48b4-9cc8-9cfa56406f15.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=A762M2VP3HO3IC9FALXZ%2F20211110%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20211110T051859Z&X-Amz-Expires=604799&X-Amz-Security-Token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NLZXkiOiJBNzYyTTJWUDNITzNJQzlGQUxYWiIsImV4cCI6MTYzNjUyMjgyMCwicG9saWN5IjoiY29uc29sZUFkbWluIn0.wt__9QLh-fq8YRWQwvcj8nro9x8CrF8sOSqpNPXJKfh7TPaZU7F7lcm_FkwMejCRcQKXkjhuGiUphYVrmGh0LQ&X-Amz-SignedHeaders=host&versionId=null&X-Amz-Signature=92b6592256640439d6c3a50d244951501ee2438f37d5e439f539cfd0152da8a7
           /* String url =
                    getInstance().getPresignedObjectUrl(new GetPresignedObjectUrlArgs().builder().bucket(bucket)
                    .object(filename).method(Method.GET).build());
            String[] urlStr = url.split("\\?");

            inputStream.close();
            if (urlStr == null || url.length() == 0 || ObjectUtils.isEmpty(urlStr[0])) {
                log.error("urlStr为null");
                return ResponseUtil.success();
            }
            URL path = new URL(urlStr[0]);
            return ResponseUtil.success(path.getPath());*/
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseUtil.fail();
        }

    }

    private boolean checkBigFileSecret(String bigFileSecret) {
        if (bigFileSecret.equals(configSecretKey)) {
            return true;
        }
        return false;
    }

    //创建bucket
    @PostMapping("createBucket")
    @ApiOperation("创建bucket")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", type = "String", value = "bucket名称", required = true,
            dataTypeClass = String.class), @ApiImplicitParam(name = "secretKey", type = "String", value = "秘钥",
            required = true, dataTypeClass = String.class),})
    public ResponseEntity createBucket(String name, String secretKey, HttpServletRequest request) {
        if (CommonUtil.checkNull(name) || name.length() < 3 || name.length() > 63) {
            return ResponseUtil.custom("名称长度3-63个字符");
        }
        if (!configSecretKey.equals(secretKey)) {
            return ResponseUtil.custom("秘钥无效");
        }
        try {

            if (checkBucketExists(name)) {
                return ResponseUtil.custom("bucket 已存在");
            }

            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(name).build();
            getInstance().makeBucket(makeBucketArgs);

            log.info("{},创建bucketName: {}", IpUtil.getIp(request), name);
            return ResponseUtil.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.fail();
        }
    }


    private boolean checkBucketExists(String bucket) throws Exception {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucket).build();
        return getInstance().bucketExists(bucketExistsArgs);
    }

    @GetMapping("getFileUrl")
    @ApiOperation("获取文件访问路径")
    public ResponseEntity getFileUrl(String fileKey) {
        if (StringUtils.isEmpty(fileKey)) {
            return ResponseUtil.custom("key为空");
        }
        String url = domain + "/" + fileKey;
        return ResponseUtil.success(url);
    }


    //endpoint: 域名或IP  ，bucket: 不同项目不同名称
    @ApiOperation("文件（图片）上传,成功返回文件地址")
    @ApiImplicitParams({@ApiImplicitParam(name = "bigFileSecret", type = "String", value = "大文件(2M以上)需要秘钥。找管理员获取",
            dataTypeClass = String.class), @ApiImplicitParam(name = "bucket", type = "String", value = "bucket" +
            "名称,提前联系管理员获取", required = true, dataTypeClass = String.class),})
    @PostMapping("uploadTgPic/{bucket}/{suffix}")
    public ResponseEntity<Map<String, Object>> uploadTgPic(@PathVariable("bucket") String bucket,
                   @PathVariable("suffix") String suffix,
                   @RequestPart("file") MultipartFile file, String bigFileSecret) {
        if (file == null) {
            return ResponseUtil.custom("文件不能为null");
        }
        try {
            if (!checkBucketExists(bucket)) {
                return ResponseUtil.custom("不支持，请联系技术");
            }
            InputStream inputStream = file.getInputStream();
            long size = inputStream.available();
            System.out.println("文件大小：" + size + " Byte");

            if (CommonUtil.checkNull(bigFileSecret) || !checkBigFileSecret(bigFileSecret)) {
                if (size == 0 || size > 2 * 1024 * 1024) {
                    return ResponseUtil.custom("文件不能大于2M");
                }
            }


            String filename = UUID.randomUUID().toString() + "." + suffix;
            PutObjectArgs args = PutObjectArgs.builder().bucket(bucket).object(filename).stream(inputStream, size,
                    -1).build();
            //上传到MINIO
            getInstance().putObject(args);
            String url = domain + "/" + bucket + "/" + filename;
            String fileKey = bucket + "/" + filename;
            Map<String, Object> data = new HashMap<>(16);
            data.put("url", url);
            data.put("fileKey", fileKey);
            return ResponseUtil.success(data);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception:{}", e.toString());
            return ResponseUtil.fail();
        }

    }


}
