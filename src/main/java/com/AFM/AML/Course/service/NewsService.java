package com.AFM.AML.Course.service;

import com.AFM.AML.Course.models.DTOs.NewsDTO;
import com.AFM.AML.Course.models.News;
import com.AFM.AML.Course.repository.NewsRepo;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.Minio.service.MinioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepo newsRepo;

    private final ImageUtil imageUtil;

    private final ObjectMapper objectMapper;

    public ResponseEntity<?> getAllNews(String type) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<News> listOfNews = newsRepo.findAllByType(type);
        return ResponseEntity.ok(listOfNews);
    }

    public List<News> getAllNewsByLang(String lang){
        List<News> listOfNews = newsRepo.findAllByType("news");
        if(Objects.equals(lang, "kz")){
            for (News news : listOfNews) {
                news.setImage(news.getKz_image());
                news.setName(news.getKz_name());
            }
        }
        if(Objects.equals(lang, "eng")){
            for (News news : listOfNews) {
                news.setImage(news.getEng_image());
                news.setName(news.getEng_name());
            }
        }
        return listOfNews;
    }

    public NewsDTO getNewsByIdByLang(Integer id,String lang) throws JsonProcessingException {
        News news=newsRepo.findById(id)
                .orElseThrow(()->new RuntimeException("News not found"));
        if(lang.equals("kz")){
            news.setImage(news.getKz_image());
            news.setName(news.getKz_name());
            news.setDescription(news.getKz_description());
        }
        if(lang.equals("eng")){
            news.setImage(news.getEng_image());
            news.setName(news.getEng_name());
            news.setDescription(news.getEng_description());
        }
        NewsDTO newsDTO=new NewsDTO(news.getId(),news.getDescription(),news.getImage(),news.getName(),news.getDate(),news.getType(),news.getLang());
        return newsDTO;
    }

    public News getNewsById(Integer id){
        return newsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
    }

    public ResponseEntity<?> createNews(String name,String kz_name,String eng_name, MultipartFile file,MultipartFile kz_file,MultipartFile eng_file,String description,String kz_description,String eng_description) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Map<String,String>> ls=objectMapper.readValue(description,List.class);
        for (Map<String, String> l : ls) {
            if (l.get("image").startsWith("data")) {
                l.put("image", imageUtil.imgAndFiles(l.get("image")));
            }
        }
        List<Map<String,String>> kz_ls=objectMapper.readValue(kz_description,List.class);
        for (Map<String, String> kzL : kz_ls) {
            if (kzL.get("image").startsWith("data")) {
                kzL.put("image", imageUtil.imgAndFiles(kzL.get("image")));
            }
        }

        List<Map<String,String>> eng_ls=objectMapper.readValue(eng_description,List.class);
        for (Map<String, String> engL : eng_ls) {
            if (engL.get("image").startsWith("data")) {
                engL.put("image", imageUtil.imgAndFiles(engL.get("image")));
            }
        }

        News news = new News();
        news.setName(name);
        news.setKz_name(kz_name);
        news.setEng_name(eng_name);
        news.setDescription(objectMapper.writeValueAsString(ls));
        news.setKz_description(objectMapper.writeValueAsString(kz_ls));
        news.setEng_description(objectMapper.writeValueAsString(eng_ls));
        news.setImage(imageUtil.fileToMinio(file));
        news.setKz_image(imageUtil.fileToMinio(kz_file));
        news.setEng_image(imageUtil.fileToMinio(eng_file));
        news.setType("news");
        news.setDate(new Date());
        newsRepo.save(news);
        return ResponseEntity.ok(news);
    }






    public News changeNew(Integer id,String name,String kz_name, String eng_name,MultipartFile file,MultipartFile kz_file,MultipartFile eng_file,String description,String kz_description,String eng_description) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        News news=newsRepo.findById(id)
                .orElseThrow(()->new RuntimeException("News not found"));

        if(name!=null){
            news.setName(name);
        }
        if(kz_name!=null){
            news.setKz_name(kz_name);
        }
        if(eng_name!=null){
            news.setEng_name(eng_name);
        }
        if(kz_file!=null){
            news.setKz_image(imageUtil.fileToMinio(kz_file));
        }
        if(file!=null){
            news.setImage(imageUtil.fileToMinio(file));
        }
        if(eng_file!=null){
            news.setEng_image(imageUtil.fileToMinio(eng_file));
        }
        List<Map<String,String>> ls=objectMapper.readValue(description,List.class);
        for (Map<String, String> l : ls) {
            if (l.get("image").startsWith("data")) {
                l.put("image", imageUtil.imgAndFiles(l.get("image")));
            }
        }
        List<Map<String,String>> kz_ls=objectMapper.readValue(kz_description,List.class);
        for (Map<String, String> kzL : kz_ls) {
            if (kzL.get("image").startsWith("data")) {
                kzL.put("image", imageUtil.imgAndFiles(kzL.get("image")));
            }
        }
        List<Map<String,String>> eng_ls=objectMapper.readValue(eng_description,List.class);
        for (Map<String, String> engL : eng_ls) {
            if (engL.get("image").startsWith("data")) {
                engL.put("image", imageUtil.imgAndFiles(engL.get("image")));
            }
        }
        news.setDescription(objectMapper.writeValueAsString(ls));
        news.setKz_description(objectMapper.writeValueAsString(kz_ls));
        news.setEng_description(objectMapper.writeValueAsString(eng_ls));
        return newsRepo.save(news);
    }

    public void deleteNew(Integer id) {
        newsRepo.deleteById(id);
    }
}
