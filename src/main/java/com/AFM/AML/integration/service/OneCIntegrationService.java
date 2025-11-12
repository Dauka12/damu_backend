package com.AFM.AML.integration.service;

import com.AFM.AML.Course.models.UserCourse;
import com.AFM.AML.Course.repository.UserCourseRepository;
import com.AFM.AML.Course.repository.CourseRepo;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import com.AFM.AML.integration.dto.ProfessionalDevelopmentDto;
import com.AFM.AML.integration.config.IntegrationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
//import java.util.Base64;

@Service
public class OneCIntegrationService {

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepo courseRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IntegrationProperties integrationProperties;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void sendFinishedToOneC() {
    List<UserCourse> finishedList = userCourseRepository.findByStatus("finished");

        for (UserCourse uc : finishedList) {
            try {
                // UserCourse holds relations to User and Course; use the related entities' ids
                if (uc.getUser() == null) continue;
                Optional<User> userOpt = userRepository.findById(uc.getUser().getUser_id());
                if (userOpt.isEmpty()) continue;
                var user = userOpt.get();

                if (uc.getCourse() == null) continue;
                var course = courseRepository.findById(uc.getCourse().getCourse_id()).orElse(null);
                if (course == null) continue;

                LocalDate startDate = uc.getPayment_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = LocalDate.parse(uc.getDate_certificate(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                String certificateNumber = "№ " + user.getUser_id() + "-" + uc.getCourse().getCourse_id() + "-" + uc.getCertificate_int();
                String fileName = certificateNumber + ".pdf";

                byte[] pdfBytes = certificateService.generateCertificatePdf(user.getUser_id(), uc.getCourse().getCourse_id());
                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        ProfessionalDevelopmentDto dto = new ProfessionalDevelopmentDto(
                        user.getIin(),
                        uc.getStatic_full_name(),
                        "Повышение квалификации в Республике Казахстан",
            "DAMU",
                        startDate.format(FORMATTER),
                        endDate.format(FORMATTER),
                        "Сертификат",
                        course.getLanguage(),
                        certificateNumber,
                        endDate.format(FORMATTER),
                        parseHours(course.getWhat_is_duration()),
                        course.getCourse_name(),
                        fileName,
                        base64Pdf
                );

                sendToOneC(dto);

                System.out.println("✅ Отправлено в 1С: " + dto.getФИО());
                //System.out.println(dto);
                //System.out.println(base64Pdf);

            } catch (Exception e) {
                System.err.println("❌ Ошибка при отправке в 1С: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void sendToOneC(ProfessionalDevelopmentDto dto) {
    // OneC expects the object itself in the request body (not wrapped in {"data": [...]}).
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

    // Read credentials and URL from configuration (application.properties)
    headers.setBasicAuth(integrationProperties.getUsername(), integrationProperties.getPassword());

    HttpEntity<ProfessionalDevelopmentDto> entity = new HttpEntity<>(dto, headers);

    ResponseEntity<String> response = restTemplate.exchange(integrationProperties.getUrl(), HttpMethod.POST, entity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Ошибка ответа 1С: " + response.getStatusCode());
        }
    }

    /**
     * Try to extract an integer number of hours from a free-form duration string.
     * If parsing fails, returns null (so DTO will omit number or send null).
     * Почему то не принимает строку поэтому применяем этот метод
     */
    private Integer parseHours(String duration) {
        if (duration == null) return null;
        try {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(duration);
            if (m.find()) {
                return Integer.valueOf(m.group(1));
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
