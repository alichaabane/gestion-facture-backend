package com.sesame.gestionfacture.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactMail {
    private String subject;
    private String content;
    private String from;
    private String to;
}
