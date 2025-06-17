package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class PostLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer post_link_id;
    private String id;

    private Date dateTime;

    private String invoiceId;
    private String invoiceIdAlt;
    private Double amount;
    private String currency;
    private String terminal;
    private String accountId;
    private String description;
    private String language;
    private String cardMask;
    private String cardType;
    private String issuer;
    private String reference;
    private String secure;
    private String tokenRecipient;
    private String code;
    private String reason;
    private Integer reasonCode;
    private String name;
    private String email;
    private String phone;
    private String ip;
    private String ipCountry;
    private String ipCity;
    private String ipRegion;
    private String ipDistrict;
    private Double ipLongitude;
    private Double ipLatitude;
    private String cardId;

}
