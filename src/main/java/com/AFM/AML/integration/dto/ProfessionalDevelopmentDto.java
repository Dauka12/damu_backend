package com.AFM.AML.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionalDevelopmentDto {
    private String ИИН;
    private String ФИО;
    private String ВидПереподготовки;
    private String УчебноеЗаведение;
    private String ДатаНачала;
    private String ДатаОкончания;
    private String ВидДокумента;
    private String ЯзыкКурса;
    private String НомерДокумента;
    private String ДатаДокумента;
    private Integer КоличествоЧасов;
    private String ТемаКурса;
    private String file_name;
    private String file_data;
}
