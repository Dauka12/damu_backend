package com.AFM.AML.Course.utils;

import com.AFM.AML.Minio.service.MinioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class ImageUtil {

    @Autowired
    MinioService minioService;

    public MultipartFile base64ToMultipartFile( String base64String) throws IOException {
        if(!base64String.startsWith("data:")){
            return null;
        }
        String[] parts = base64String.split(",");
        String contentType = parts[0].split(";")[0].split(":")[1];
        String base64Data = parts[1];
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(base64Data);
        return new MockMultipartFile("file", "filename", contentType, decodedBytes);
    }

    public String imgAndFiles(String data) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(Objects.equals(data, "")){
            return "";
        }
        StringBuilder sb = new StringBuilder(data);
        if(data.charAt(0)=='"'){
            sb.deleteCharAt(data.length() - 1);
            sb.deleteCharAt(0);
        }
        String baseFile =sb.toString();

        MultipartFile file=base64ToMultipartFile(baseFile);
        if(file!=null){
            String filename=UUID.randomUUID().toString();
            minioService.uploadFile(file,filename);
            return "http://192.168.122.132:9000/aml/"+filename;
        }
        return data;
    }

    public String fileToMinio(MultipartFile file){
        String filename= UUID.randomUUID().toString();
        try {
            minioService.uploadFile(file,filename);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "http://192.168.122.132:9000/aml/"+filename;
    }

    public String images(String data) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String[] files=data.split("\"");
        System.out.println(files.length);
        StringBuilder ans= new StringBuilder();
        for (int i=0;i< files.length;i++){
            if(i%2==1){
                ans.append('"').append(imgAndFiles(files[i])).append('"').append(",");
            }
        }
        ans.deleteCharAt(ans.length()-1);
//        ans.deleteCharAt(ans.length()-1);
        ans.append("]");
        ans.insert(0,"[");
        System.out.println(ans);
        return ans.toString();
    }

    public  String jsonFormImages(String data,String type) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(data);
        if (rootNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) rootNode;
            for (JsonNode element : arrayNode) {
                if (element.isObject()) {
                    ObjectNode objectNode = (ObjectNode) element;
                    if(type.equals("stages")){
                        String icon = objectNode.get("icon").asText();
                        String manipulatedIcon = imgAndFiles(icon);
                        objectNode.put("icon", manipulatedIcon);
                    }else if(type.equals("data")){
                        String icon = objectNode.get("image").asText();
                        String manipulatedIcon = imgAndFiles(icon);
                        objectNode.put("image", manipulatedIcon);
                    }
                }
            }
            return objectMapper.writeValueAsString(arrayNode);
        } else {
            throw new IOException("The root JSON node is not an array.");
        }
    }

    public String dynamicTypes(String data) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String regex = "\"data:.*?\"";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher for the input string
        Matcher matcher = pattern.matcher(data);

        // Find and print all matches
        StringBuffer resultString = new StringBuffer();

        // Find and replace all matches
        while (matcher.find()) {
            // Replace the matched substring with matcher.group()
            matcher.appendReplacement(resultString, "\""+imgAndFiles(matcher.group())+"\"");
        }

        // Append the rest of the string
        matcher.appendTail(resultString);

        // Print the modified string
//        System.out.println("Modified string: " + resultString.toString());
        return resultString.toString();
    }


}