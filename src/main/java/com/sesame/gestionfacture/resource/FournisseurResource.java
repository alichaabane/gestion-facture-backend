package com.sesame.gestionfacture.resource;

import com.sesame.gestionfacture.dto.FournisseurDTO;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.ProduitDTO;
import com.sesame.gestionfacture.service.FournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurResource {

    @Autowired
    private FournisseurService fournisseurService;

    @GetMapping
    public ResponseEntity<List<FournisseurDTO>> getAllFournisseurs() {
        List<FournisseurDTO> fournisseurs = fournisseurService.getAllFournisseurs();
        return new ResponseEntity<>(fournisseurs, HttpStatus.OK);
    }

    @GetMapping("/count")
    public int getTotalFournisseurs() {
        return fournisseurService.countFournisseurs();
    }

    @GetMapping("/paginated")
    public ResponseEntity<PageRequestData<?>> getAllFournisseursPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        PageRequestData<FournisseurDTO> fournisseurs = fournisseurService.getAllFournisseursPaginated(pageRequest);
        if(fournisseurs != null){
            return new ResponseEntity<>(fournisseurs, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Void> addFournisseur(@RequestBody FournisseurDTO fournisseurDTO) {
        fournisseurService.addFournisseur(fournisseurDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{fournisseurId}")
    public ResponseEntity<Void> deleteFournisseur(@PathVariable Long fournisseurId) {
        if (fournisseurService.deleteFournisseur(fournisseurId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{fournisseurId}")
    public ResponseEntity<FournisseurDTO> updateFournisseur(@PathVariable Long fournisseurId, @RequestBody FournisseurDTO newFournisseurDTO) {
        newFournisseurDTO.setId(fournisseurId);
        FournisseurDTO updatedFournisseur = fournisseurService.updateFournisseur(newFournisseurDTO);

        if (updatedFournisseur != null) {
            return new ResponseEntity<>(updatedFournisseur, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{fournisseurId}")
    public ResponseEntity<FournisseurDTO> getFournisseurById(@PathVariable Long fournisseurId) {
        FournisseurDTO fournisseur = fournisseurService.getFournisseurById(fournisseurId);

        if (fournisseur != null) {
            return new ResponseEntity<>(fournisseur, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
