package com.sesame.gestionfacture.service.impl;

import com.sesame.gestionfacture.dto.FournisseurDTO;
import com.sesame.gestionfacture.entity.Fournisseur;
import com.sesame.gestionfacture.mapper.FournisseurMapper;
import com.sesame.gestionfacture.repository.FournisseurRepository;
import com.sesame.gestionfacture.service.FournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FournisseurServiceImpl implements FournisseurService {

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private FournisseurMapper fournisseurMapper;

    @Override
    public List<FournisseurDTO> getAllFournisseurs() {
        List<Fournisseur> fournisseurs = fournisseurRepository.findAll();
        return fournisseurs.stream()
                .map(fournisseurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addFournisseur(FournisseurDTO fournisseurDTO) {
        Fournisseur fournisseur = fournisseurMapper.toEntity(fournisseurDTO);
        fournisseurRepository.save(fournisseur);
    }

    @Override
    public boolean deleteFournisseur(Long fournisseurId) {
        if (fournisseurRepository.existsById(fournisseurId)) {
            fournisseurRepository.deleteById(fournisseurId);
            return true;
        }
        return false;
    }

    @Override
    public FournisseurDTO updateFournisseur(FournisseurDTO newFournisseurDTO) {
        Fournisseur existingFournisseur = fournisseurRepository.findById(newFournisseurDTO.getId()).orElse(null);

        if (existingFournisseur != null) {
            fournisseurMapper.fillEntity(newFournisseurDTO, existingFournisseur);
            fournisseurRepository.save(existingFournisseur);
            return fournisseurMapper.toDto(existingFournisseur);
        }

        return null;
    }

    @Override
    public FournisseurDTO getFournisseurById(Long fournisseurId) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId).orElse(null);
        return (fournisseur != null) ? fournisseurMapper.toDto(fournisseur) : null;
    }
}
