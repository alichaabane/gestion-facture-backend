package com.sesame.gestionfacture.resource;

import com.sesame.gestionfacture.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/facture")
public class FactureResource {

    @Autowired
    private FactureService factureService;
    @PostMapping()
    public void generatePdfFileForFacture(){

        String pdfFilename = "Facture.pdf";

        factureService.createPdf(pdfFilename);

    }

}
