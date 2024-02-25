package com.sesame.gestionfacture.resource;

import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.dto.FournisseurDTO;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/count")
    public int getTotalFactures() {
        return factureService.countFactures();
    }
    @GetMapping("/paginated")
    public ResponseEntity<PageRequestData<?>> getAllFacturesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        PageRequestData<FactureDTO> factures = factureService.getAllFacturesPaginated(pageRequest);
        if(factures != null){
            return new ResponseEntity<>(factures, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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
