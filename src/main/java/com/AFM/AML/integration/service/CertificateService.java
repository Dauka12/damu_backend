package com.AFM.AML.integration.service;

import com.AFM.AML.Course.models.UserCourse;
import com.AFM.AML.Course.repository.UserCourseRepository;
import com.AFM.AML.Course.service.QrCodeGenerator;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import com.AFM.AML.integration.models.CertificateLog;
import com.AFM.AML.integration.repository.CertificateLogRepository;
import com.google.zxing.WriterException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    @Autowired
    private CertificateLogRepository certificateLogRepository;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    /**
     * Генерация PDF и логирование отправки.
     * Если уже отправлен — повторно не делает.
     */
    public byte[] generateCertificatePdf(int userId, int courseId)
            throws IOException, DocumentException, WriterException {

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) throw new RuntimeException("User not found: " + userId);
        User user = userOpt.get();

        UserCourse userCourse = userCourseRepository.findByUserAndCourseId(userId, courseId);
        if (userCourse == null) throw new RuntimeException("UserCourse not found");

        String certificateNumber = "№ " + user.getUser_id() + "-" + courseId + "-" + userCourse.getCertificate_int();

        // === Проверяем, не отправляли ли уже ===
        Optional<CertificateLog> existing = certificateLogRepository
                .findFirstByUserIdAndCourseIdAndStatus(userId, courseId, "SENT");
        if (existing.isPresent()) {
            System.out.println("⚠️ Сертификат уже отправлен ранее: " + certificateNumber);
            return null; // или можно вернуть existing.get().getPdfBytes(), если ты хранишь PDF в БД
        }

        // === Генерация PDF ===
        InputStream inputStream = getClass().getResourceAsStream("/ceee.pdf");
        PdfReader pdfReader = new PdfReader(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);
        PdfContentByte pdfContentByte = pdfStamper.getOverContent(1);

        InputStream fontStream = getClass().getResourceAsStream("/ARIALUNI.TTF");
        BaseFont baseFont = BaseFont.createFont("ARIALUNI.TTF", BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED, true, fontStream.readAllBytes(), null);

        String date = userCourse.getDate_certificate();
        int rester = userCourse.getCertificate_int();
        String fullName = userCourse.getStatic_full_name();
        String courseName = userCourse.getCourse().getCourse_name();

        // === Разбиваем длинное название курса ===
        List<String> courseNameLines = new ArrayList<>();
        int maxLineLength = 45;
        if (courseName.length() <= maxLineLength) {
            courseNameLines.add(courseName);
        } else {
            String[] words = courseName.split(" ");
            StringBuilder currentLine = new StringBuilder();
            for (String word : words) {
                if (currentLine.length() + word.length() + 1 <= maxLineLength) {
                    if (currentLine.length() > 0) currentLine.append(" ");
                    currentLine.append(word);
                } else {
                    courseNameLines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            }
            if (currentLine.length() > 0) courseNameLines.add(currentLine.toString());
        }

        // === Формируем текст для PDF ===
        List<String> textLines = new ArrayList<>();
        textLines.add("");
        textLines.add("");
        textLines.add(StringUtils.center("«DAMU» онлайн оқыту платформасында", 60));
        textLines.add(StringUtils.center(fullName, 90));
        textLines.add("");
        for (String line : courseNameLines) textLines.add(StringUtils.center(line, 60));
        textLines.add(StringUtils.center("курсынан қашықтықтан оқу форматында сәтті өткенін растайды", 60));
        textLines.add("");
        textLines.add(StringUtils.center("подтверждает успешное прохождение курса", 60));
        textLines.add(StringUtils.center("на обучающей онлайн платформе «DAMU»", 60));
        for (String line : courseNameLines) textLines.add(StringUtils.center(line, 60));
        textLines.add(StringUtils.center("в дистанционном формате", 60));
        textLines.add("");
        textLines.add(StringUtils.center("Берілген күні/ Дата получения: " + date, 60));

        String[] russianText = textLines.toArray(new String[0]);
        int fontSize = courseNameLines.size() <= 1 ? 16 :
                courseNameLines.size() <= 2 ? 15 :
                        courseNameLines.size() <= 3 ? 14 : 13;

        float x = 425;
        float y = 410;

        pdfContentByte.beginText();
        pdfContentByte.setFontAndSize(baseFont, fontSize);
        for (int i = 0; i < russianText.length; i++) {
            pdfContentByte.showTextAligned(Element.ALIGN_CENTER, russianText[i], x, y - (i * 22), 0);
        }
        pdfContentByte.endText();

        // === QR-код ===
        String url = publicBaseUrl.replaceAll("/+$", "") + "/api/checkQR/" + user.getUser_id() + "/" + courseId;
        byte[] qr = qrCodeGenerator.getQrCode(url, 100, 100);
        Image qrImage = Image.getInstance(qr);
        qrImage.setAbsolutePosition(710, 40);
        pdfContentByte.addImage(qrImage);

        // === Номер сертификата ===
        pdfContentByte.beginText();
        pdfContentByte.setFontAndSize(baseFont, 8);
        pdfContentByte.showTextAligned(Element.ALIGN_CENTER, "Номер сертификата:", 760, 50, 0);
        pdfContentByte.showTextAligned(Element.ALIGN_CENTER, certificateNumber, 760, 38, 0);
        pdfContentByte.endText();

        pdfStamper.close();
        pdfReader.close();

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();

        // === Логируем успешную генерацию ===
        CertificateLog log = CertificateLog.builder()
                .userId(userId)
                .courseId(courseId)
                .certificateNumber(certificateNumber)
                .status("SENT")
                .responseJson("{\"message\": \"certificate sent\"}")
                .sendTime(LocalDateTime.now())
                .build();

        certificateLogRepository.save(log);

        System.out.println("✅ Сертификат сгенерирован и записан в лог: " + certificateNumber);

        return pdfBytes;
    }
}
