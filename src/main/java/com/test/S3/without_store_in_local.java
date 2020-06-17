package com.kv.eco.common.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Random;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

public class EcoS3Util {
    static BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA4GFCLUFG2AZNC5GR", "Ld5ntUof+YQK4juhxKAMcRWxsS/xxPw5LaLl/QFm");
    static AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("ap-east-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    static String bucketName = "kv-pilltech";
    public static String Upload(String pathFileName,String base64)
    {
        try {
            if(base64==null || base64=="")
                return "";
            byte[] bytes = Base64.getDecoder().decode(base64.getBytes());
            InputStream fis = new ByteArrayInputStream(bytes);
            ObjectMetadata metadata = new ObjectMetadata();
            long todayMillis = System.currentTimeMillis() / 1000L;
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String month=new SimpleDateFormat("MMM").format(Calendar.getInstance().getTime());
            Random rand = new Random();
            int random = rand.nextInt(10000000);
            String mimeType = URLConnection.guessContentTypeFromStream(fis);
            pathFileName+="/"+year+"/"+month+"/"+todayMillis+""+random+"."+mimeType.split("/")[1];
            metadata.setContentLength(bytes.length);
            metadata.setContentType(mimeType);
            metadata.setCacheControl("public, max-age=31536000");
            s3Client.putObject(bucketName, pathFileName, fis, metadata);
            s3Client.setObjectAcl(bucketName, pathFileName, CannedAccessControlList.PublicRead);
            System.out.println("--Uploading file done");
            return pathFileName;
        } catch (IOException | AmazonS3Exception e) {
            e.printStackTrace();
            System.out.println("--Uploading file Fail");
            return "false";
        }
    }

    public static boolean Download(String pathFileName)
    {
        try{
            // SECTION 3: Get file from S3 bucket
            s3Client.getObject(new GetObjectRequest(bucketName, pathFileName));
            System.out.println("--Downloading file done");
            return true;
        }catch(AmazonS3Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
