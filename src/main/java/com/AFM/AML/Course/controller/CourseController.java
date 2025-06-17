package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.models.*;
import com.AFM.AML.Course.models.DTOs.CourseBasicInfoDTO;
import com.AFM.AML.Course.models.DTOs.CourseByIdDTO;
import com.AFM.AML.Course.models.DTOs.CourseNoBasicInfoDTO;
import com.AFM.AML.Course.models.DTOs.NewsRequestDTO;
import com.AFM.AML.Course.service.CourseService;
import com.AFM.AML.Course.service.NewsService;
import com.AFM.AML.Course.service.QrCodeGenerator;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.Minio.service.MinioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;

import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://192.168.122.132:3000", allowCredentials = "true")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

@RequestMapping("/api/aml/course")
@RequiredArgsConstructor
public class CourseController {

    @Autowired
    CourseService courseService;
    @Autowired
    MinioService minioService;

    @Autowired
    NewsService newsService;

    @Autowired
    QrCodeGenerator qrCodeGenerator;

    @Autowired
    ImageUtil imageUtil;

    @GetMapping("/getUserBazovii")
    public ResponseEntity<?> getUserBazov(){
        return ResponseEntity.ok(courseService.getUserBazovii());
    }
    //-----------------NEWS----------------------------
    @GetMapping("/getAllNews")
    public ResponseEntity<?> getAllNews(@RequestParam String type) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return newsService.getAllNews(type);
    }

    @GetMapping("/getAllNewsByLang/{lang}")
    public ResponseEntity<?> getAllNewsByLang(@PathVariable String lang){
        return ResponseEntity.ok(newsService.getAllNewsByLang(lang));
    }

    @GetMapping("/getNewsById")
    public ResponseEntity<?> getNewsById(@RequestParam Integer id){
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @GetMapping(value="/getNewsById/{lang}", produces = "application/json")
    public ResponseEntity<?> getNewsByIdByLang(@RequestParam Integer id, @PathVariable String lang) throws JsonProcessingException {
        return ResponseEntity.ok(newsService.getNewsByIdByLang(id, lang));
    }

    @PostMapping("/createNews")
    public ResponseEntity<?> createNews(@RequestParam String name,@RequestParam String kz_name,@RequestParam String eng_name,@RequestParam MultipartFile file,@RequestParam MultipartFile kz_file,@RequestParam MultipartFile eng_file,@RequestParam String description,@RequestParam String kz_description,@RequestParam String eng_description) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return newsService.createNews(name,kz_name,eng_name,file,kz_file,eng_file,description,kz_description,eng_description);
    }

    @PutMapping("/editNews")
    public ResponseEntity<?> editNews(@RequestParam Integer id,@RequestParam String name,@RequestParam String kz_name,@RequestParam String eng_name,@RequestParam(required = false) MultipartFile file,@RequestParam(required = false) MultipartFile kz_file,@RequestParam(required = false) MultipartFile eng_file,@RequestParam String description,@RequestParam String kz_description,@RequestParam String eng_description) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(newsService.changeNew(id,name,kz_name,eng_name,file,kz_file,eng_file,description,kz_description,eng_description));
    }

    @DeleteMapping("/deleteNews")
    public ResponseEntity<?> deleteNews(@RequestParam Integer id) throws JsonProcessingException {
        newsService.deleteNew(id);
        return ResponseEntity.ok("News deleted successfully");
    }

    @GetMapping("/getUsersAndCourses")
    public ResponseEntity<?> getUsersAndCourses() {
        return courseService.getUsersAndCourses();
    }

    @GetMapping("/editcatalog")
    public ResponseEntity<Object> catalogEdit() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return courseService.getCatalog();
    }

    @GetMapping("/basicInfoCourse")
    public ResponseEntity<Object> getBasicdawdawdInfo(@RequestParam int id) {
        return courseService.getCourseBasicInfo(id);
    }

    @PostMapping("/updateBasicInfo/{id}")
    public ResponseEntity<Object> updateBasicInfo(@RequestBody CourseBasicInfoDTO course, @PathVariable int id) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return courseService.updateCourseBasicInfo(course, id);
    }
    @PostMapping("/updateNoBasicInfo/{id}")
    public ResponseEntity<Object> updateNoBasicInfo(@RequestBody CourseNoBasicInfoDTO course, @PathVariable int id) {
        return courseService.updateCourseNoBasicInfo(course, id);
    }

    @PostMapping("/publishCourse")
    public ResponseEntity<Object> publishCourse(@RequestParam int id) {
        return courseService.publishCourse(id);
    }

    @PostMapping("/deleteCourse")
    public ResponseEntity<Object> deleteCourse(@RequestParam int id) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return courseService.deleteCourse(id);
    }
    @PostMapping("/createPostLink")
    public ResponseEntity<?> fefef(@RequestBody PostLink postLink){
        courseService.createPostLink(postLink);
        return ResponseEntity.ok("ok");
    }
    @GetMapping("/getPostLink/{inovice_id}")
    public ResponseEntity<?> getpostlink(@PathVariable String inovice_id){
        return courseService.returnPostLink(inovice_id);
    }
    @GetMapping("/invoiceID")
    public ResponseEntity<?> getRandomDigits(Principal principal){
        return courseService.getUsersInvoice(principal);
    }
    @PostMapping("/saveBasicInfoDraft")
    public ResponseEntity<Integer> saveDraft(@RequestBody CourseBasicInfoDTO course) {
        try {
            String filename=UUID.randomUUID().toString();
            MultipartFile file=imageUtil.base64ToMultipartFile(course.getImage());
            if(!(file ==null)){
                course.setImage(filename);
                minioService.uploadFile(file,filename);
            }
            ResponseEntity<Course> courseDraft = courseService.createDraftBasicInfo(course);
            if (courseDraft.getBody() != null) {
                int courseId = courseDraft.getBody().getCourse_id();
                return ResponseEntity.ok(courseId);
//                return ResponseEntity.status(HttpStatus.OK).body(file);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(-1); // You can choose an appropriate error code or handle it differently
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(-1); // You can choose an appropriate error code or handle it differently
        }
    }

    @PostMapping("/saveCourse")
    public String SaveCourse(@RequestParam String course_name,
                             @RequestParam double course_price,
                             @RequestParam int courseCategoryID,
                             @RequestParam String course_for_member_of_the_system
            , @RequestParam("file") MultipartFile file) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidExpiresRangeException {
        String objectName = UUID.randomUUID().toString();
        minioService.uploadFile(file,objectName);
        Course course = new Course().builder().course_price(course_price).course_name(course_name).course_image(objectName).course_for_member_of_the_system(course_for_member_of_the_system).build();
        courseService.saveCourse(course,courseCategoryID);
        return "Course saved" + course;
    }
    @PutMapping("/saveUser/{userId}/course/{courseId}")
    public ResponseEntity<?> SaveCourseToUser(@PathVariable int userId, @PathVariable int courseId){
        return courseService.addUsertoCourse(userId,courseId);
    }



    @PutMapping("/saveUsersToPravohranka")
    public ResponseEntity<?> SavetoPravo(){
        return courseService.addUserstoCourse();
    }
    @PostMapping("/saveUserRequest/course/{courseId}")
    public ResponseEntity<?> SaveCourseToUser(@PathVariable int courseId,@RequestParam("userCourse") String model) throws JsonProcessingException {
        return courseService.addUsertoCourseWithCount(courseId,model);
    }
    @GetMapping("/getRequest")
    public List<UserCourse> getttef(){
        return courseService.getUsersAndCoursesRequest();
    }
    @PostMapping("/saveCourseCategory")
    public String SaveCourse(@RequestBody CourseCategory courseCategory){
        courseService.saveCourseCategory(courseCategory);
        return "CourseCategory saved" + courseCategory;
    }
    @PostMapping("/createCourseComments/{course_id}")
    public String createComments(@RequestBody CourseComments courseComments,Principal principal,@PathVariable int course_id){
        courseService.createCourseComments(courseComments,principal,course_id);
        return "CourseComments saved" + courseComments;
    }
    @GetMapping("/getCoursePercentage/{course_id}")
    public ResponseEntity<?> findPercentage(Principal principal,@PathVariable int course_id){
        return courseService.getUserCourse(principal,course_id);
    }

    @GetMapping("/getUserCourses")
    public ResponseEntity<?> findUsersCourses(Principal principal) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(courseService.findUsersCourses(principal));
    }

    @GetMapping("/getUserUsingCourses")
    public ResponseEntity<?> getProcessingCourses(Principal principal) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(courseService.getProcessingCourses(principal));
    }
    @GetMapping("/getUserCoursesNoPr")
    public ResponseEntity<?> findUsersCoursess() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(courseService.findUsersCoursesNoPr());
    }

    @GetMapping("/getCoursesPage")
    public ResponseEntity<?> getCoursesByPageable(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "3") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(courseService.getCourseByPageable(pageable));
    }

    @GetMapping("/getCourses")
    public ResponseEntity<List<Course>> GetCourses() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidExpiresRangeException {
        List<Course> courses = courseService.getCourses();
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/getCourseCategories")
//    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<List<CourseCategory>> GetCourseCategories() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(courseService.getCourseCategory());
    }
    @DeleteMapping("/deleteCourse")
    public void DeleteCourse(@RequestBody Map<String, Integer> id){
        courseService.deleteCourse(id.get("id"));
    }

    @PutMapping("/updateCourse/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable int id, @RequestBody Course course){
        Optional<Course> course1 = courseService.findCourseById(id);
        course1.get().setCourseCategory(course.getCourseCategory());
        course1.get().setCourse_image(course.getCourse_image());
        course1.get().setCourse_price(course.getCourse_price());
        course1.get().setCourseCategory(course.getCourseCategory());
        courseService.saveCourse(course1.get());
        return ResponseEntity.ok(course);
    }
    //    @GetMapping("/getCourseById/{course_id}")
//    public ResponseEntity<?> updateCourse(@PathVariable int course_id,Principal principal) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        return ResponseEntity.ok(courseService.findCourseByIdd(principal,course_id));
//    }
    @GetMapping("/getCourseById/{course_id}")
    public ResponseEntity<?> getCourseById(@PathVariable int course_id,Principal principal) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(courseService.findCourseByIdd(principal,course_id));
    }

    @GetMapping("/justGetCourseById/{course_id}")
    public ResponseEntity<?> getCourseBB(@PathVariable  int course_id){
        return ResponseEntity.ok(courseService.getCourseByID(course_id));
    }
    @GetMapping("/getCourseForUser/{course_id}")
    public ResponseEntity<?> getCoursePreview(@PathVariable int course_id, Principal principal) {
        CourseByIdDTO course1 = courseService.getCourseForUser(course_id, principal);;
        return ResponseEntity.ok(course1);
    }
    @GetMapping("/getCertificateByCourseId/{course_id}")
    public ResponseEntity<byte[]> editPdf(Principal principal,@PathVariable int course_id) {
        return courseService.editPdf(principal,course_id);
    }

    @GetMapping("/getQrCode")
    public ResponseEntity<?> getQr() throws IOException, WriterException {
        String url = "https://medium.com/nerd-for-tech/how-to-generate-qr-code-in-java-spring-boot-134adb81f10d";
        byte[] image = new byte[0];
        image = qrCodeGenerator.getQrCode(url, 250, 250);
        return ResponseEntity.ok(image);
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generatePdf() {
        try {

            PDDocument document = PDDocument.load(new File("src\\main\\resources\\ceee.pdf"));

            PDPage page = document.getPage(0);

            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

            contentStream.beginText();
            //Setting the font to the Content stream
            PDType0Font pdfFont = PDType0Font.load( document, new File( "src\\main\\resources\\ARIALUNI.TTF" ) );
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 100);
            contentStream.newLineAtOffset(1000, 1700);

            // Add text to the existing page
            contentStream.showText("Text added to existing PDF");

            contentStream.endText();

            // Close the content stream
            contentStream.close();

            // Save the PDF as a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            document.close();

            // Set the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "output.pdf");

            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    public void editText(PdfPageBase page,String newText,String findText){
//        PdfTextFindCollection collection = page.findText(findText,false);
//
//        //Define new text for replacing
//        //Create a PdfTrueTypeFont object based on a specific used font
//        PdfTrueTypeFont font = new PdfTrueTypeFont(new Font("Aurel", 0, 22)); // Adjust font size and style
//        Dimension2D dimension2D = font.measureString(newText);
//        double fontWidth = dimension2D.getWidth();
//        double height = dimension2D.getHeight();
//
//        for (Object findObj : collection.getFinds()) {
//            PdfTextFind find=(PdfTextFind)findObj;
//            List<Rectangle2D> textBounds = find.getTextBounds();
//
//            //Draw a white rectangle to cover the old text
//            Rectangle2D rectangle2D = textBounds.get(0);
//            new Rectangle((int)rectangle2D.getX(),(int)rectangle2D.getY(),(int)fontWidth,(int)height);
//            page.getCanvas().drawRectangle(PdfBrushes.getTransparent(), rectangle2D);
//
//            //Draw new text at the position of the old text
//            page.getCanvas().drawString(newText, font, PdfBrushes.getBlack(), rectangle2D.getX()-140, rectangle2D.getY() );
//        }
//    }
}
