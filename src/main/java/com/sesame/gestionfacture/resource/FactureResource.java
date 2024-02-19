package com.sesame.gestionfacture.resource;

import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facture")
public class FactureResource {

    @Autowired
    private FactureService factureService;

    @GetMapping
    public ResponseEntity<List<FactureDTO>> getAllFactures() {
        List<FactureDTO> produits = factureService.getAllFactures();
        return new ResponseEntity<>(produits, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Void> addFacture(@RequestBody FactureDTO factureDTO) {
        factureService.addFacture(factureDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{factureId}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long factureId) {
        if (factureService.deleteFactureById(factureId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

/*
    @PostMapping("/test")
    public void generatePdfFileForFacture(){
        String pdfFilename = "Facture.pdf";
        factureService.createPdf(pdfFilename);
    }


 */
}
