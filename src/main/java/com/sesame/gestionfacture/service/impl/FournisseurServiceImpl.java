package com.sesame.gestionfacture.service.impl;

import com.sesame.gestionfacture.dto.FournisseurDTO;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.entity.Fournisseur;
import com.sesame.gestionfacture.mapper.FournisseurMapper;
import com.sesame.gestionfacture.repository.FournisseurRepository;
import com.sesame.gestionfacture.service.FournisseurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FournisseurServiceImpl implements FournisseurService {

    private final Logger logger = LoggerFactory.getLogger(FournisseurServiceImpl.class);

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

    @Override
    public PageRequestData<FournisseurDTO> getAllFournisseursPaginated(PageRequest pageRequest) {
        Page<Fournisseur> fournisseurPage = fournisseurRepository.findAll(pageRequest);
        PageRequestData<FournisseurDTO> customPageResponse = new PageRequestData<>();
        customPageResponse.setContent(fournisseurPage.map(fournisseurMapper::toDto).getContent());
        customPageResponse.setTotalPages(fournisseurPage.getTotalPages());
        customPageResponse.setTotalElements(fournisseurPage.getTotalElements());
        customPageResponse.setNumber(fournisseurPage.getNumber());
        customPageResponse.setSize(fournisseurPage.getSize());
        logger.info("Fetching All fournisseurs of Page N° " + pageRequest.getPageNumber());
        return customPageResponse;
    }
}
