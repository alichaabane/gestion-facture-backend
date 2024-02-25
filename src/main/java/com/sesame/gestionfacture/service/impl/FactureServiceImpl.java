package com.sesame.gestionfacture.service.impl;

import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.sesame.gestionfacture.dto.Facture2DTO;
import com.sesame.gestionfacture.dto.FactureDTO;
import com.sesame.gestionfacture.dto.PageRequestData;
import com.sesame.gestionfacture.dto.ProduitDTO;
import com.sesame.gestionfacture.entity.Facture;
import com.sesame.gestionfacture.entity.Produit;
import com.sesame.gestionfacture.mapper.Facture2Mapper;
import com.sesame.gestionfacture.mapper.FactureMapper;
import com.sesame.gestionfacture.mapper.ProduitMapper;
import com.sesame.gestionfacture.repository.FactureRepository;
import com.sesame.gestionfacture.repository.ProduitRepository;
import com.sesame.gestionfacture.service.FactureService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sesame.gestionfacture.service.ProduitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FactureServiceImpl implements FactureService {

    private final Logger logger = LoggerFactory.getLogger(FactureServiceImpl.class);

    private List<ProduitDTO> produitDTOS;

    @Autowired
    private FactureRepository factureRepository;
    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private FactureMapper factureMapper;
    @Autowired
    private Facture2Mapper facture2Mapper;

    @Autowired
    private ProduitMapper produitMapper;

    @Autowired
    private ProduitService produitService;

    @Value("${external.facturestoragePath}")
    private String factureDir;

    @Override
    public List<FactureDTO> getAllFactures() {
        List<Facture> factures = factureRepository.findAll();
        return factures.stream()
                .map(factureMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] addFacture(FactureDTO factureDTO) {
        Facture facture = factureMapper.toEntity(factureDTO);
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.FRENCH);
        String strDate = date.format(formatter);

        LocalDateTime parsedDate = LocalDateTime.parse(strDate, formatter);
        facture.setCreatedAt(parsedDate);

        Facture factureSaved = factureRepository.save(facture);

        this.produitDTOS = facture.getListeProduits().stream().map(produitMapper::toDto).collect(Collectors.toList());
        List<ProduitDTO> produitForFacture = produitService.getProduitsByFacture(factureSaved.getId());
       return generatePdf(factureSaved.getId()+"_"+factureSaved.getNomClient()+" "+factureSaved.getPrenomClient()+".pdf",produitForFacture,factureDTO);
    }

    @Override
    public boolean deleteFactureById(Long id) {
        Optional<Facture> optionalFacture = factureRepository.findById(id);

        if (optionalFacture.isPresent()) {
            Facture facture = optionalFacture.get();

            // Update Produit entities associated with the Facture
            List<Produit> produits = facture.getListeProduits();
            for (Produit produit : produits) {
                produit.setFacture(null);
                produitRepository.save(produit);
            }

            // Delete the Facture
            factureRepository.deleteById(id);
            deletePdfFile(id);

            return true;
        }

        return false;
    }


     @Override
     public void deletePdfFile(Long factureId) {
         // Define the directory where PDF files are stored
         String pdfDirectory = factureDir; // Update with your actual directory

         try {
             // Create a File object representing the PDF directory
             File directory = new File(factureDir);

             // Check if the directory exists
             if (directory.exists() && directory.isDirectory()) {
                 // List all files in the directory
                 File[] files = directory.listFiles();

                 // Loop through files and delete the one starting with factureId
                 assert files != null;
                 for (File file : files) {
                     if (file.getName().startsWith(factureId.toString())) {
                         // Attempt to delete the file
                         if (file.delete()) {
                             System.out.println("PDF file deleted successfully: " + file.getName());
                         } else {
                             System.out.println("Failed to delete PDF file: " + file.getName());
                         }
                     }
                 }
             } else {
                 System.out.println("PDF directory not found: " + pdfDirectory);
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

    @Override
    public int countFactures() {
        return (int) factureRepository.count();
    }


    public byte[] generatePdf(String fileName, List<ProduitDTO> listeProduits, FactureDTO factureDTO) {
        try {

            OutputStream file = new FileOutputStream(new File(factureDir + fileName));
            Document document = new Document();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteArrayOutputStream);

            PdfWriter.getInstance(document, file);

            //Inserting Image in PDF
            Image image = Image.getInstance ("src/main/resources/logo.jpg");//Header Image
            image.scaleAbsolute(540f, 72f);//image width,height

            //Date de la facture
            LocalDateTime date = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
            String strDate = date.format(formatter);


            PdfPTable irdTable = new PdfPTable(2);
            irdTable.addCell(getIRDCell("N° Facture"));
            irdTable.addCell(getIRDCell("Date facture"));
            irdTable.addCell(getIRDCell("XXXX")); // pass invoice number
            irdTable.addCell(getIRDCell(strDate)); // pass invoice date

            PdfPTable irhTable = new PdfPTable(3);
            irhTable.setWidthPercentage(108);

            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("Facture", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
            PdfPCell invoiceTable = new PdfPCell (irdTable);
            invoiceTable.setBorder(0);
            irhTable.addCell(invoiceTable);

            FontSelector fs = new FontSelector();
            Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13, Font.BOLD);
            fs.addFont(font);
            Phrase bill = fs.process("Facturer à"); // customer information
            Paragraph name = new Paragraph(StringUtils.capitalize(factureDTO.getNomClient())+" "+StringUtils.capitalize(factureDTO.getPrenomClient()));
            name.setIndentationLeft(20);
            Paragraph contact = new Paragraph("+216 "+ factureDTO.getNumTelClient());
            contact.setIndentationLeft(20);
            Paragraph address = new Paragraph(factureDTO.getAdresseClient());
            address.setIndentationLeft(20);

            PdfPTable billTable = new PdfPTable(6); //one page contains 15 records
            billTable.setWidthPercentage(100);
            billTable.setWidths(new float[] { 1, 2,5,2,1,2 });
            billTable.setSpacingBefore(30.0f);
            billTable.addCell(getBillHeaderCell("Index"));
            billTable.addCell(getBillHeaderCell("Produit"));
            billTable.addCell(getBillHeaderCell("Description"));
            billTable.addCell(getBillHeaderCell("Prix unitaire"));
            billTable.addCell(getBillHeaderCell("Qté"));
            billTable.addCell(getBillHeaderCell("Montant"));

            //Looping through all products to print them in the PDF
            AtomicInteger i= new AtomicInteger();
            AtomicReference<Double> totalPrix = new AtomicReference<>((double) 0);
            listeProduits.forEach(produitDTO -> {
                totalPrix.updateAndGet(v -> v + produitDTO.getPrix());
                i.getAndIncrement();
                billTable.addCell(getBillRowCell(String.valueOf(i)));
                billTable.addCell(getBillRowCell(produitDTO.getNom()));
                billTable.addCell(getBillRowCell(produitDTO.getDescription()));
                billTable.addCell(getBillRowCell(String.valueOf(produitDTO.getPrix())));
                billTable.addCell(getBillRowCell("1"));
                billTable.addCell(getBillRowCell(String.valueOf(produitDTO.getPrix())));
            });


            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            billTable.addCell(getBillRowCell(" "));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));
            billTable.addCell(getBillRowCell(""));

            PdfPTable validity = new PdfPTable(1);
            validity.setWidthPercentage(100);
            validity.addCell(getValidityCell(" "));
            validity.addCell(getValidityCell("Garantie"));
            validity.addCell(getValidityCell(" * Les produits achetés sont accompagnés d'une garantie nationale d'un an \n   (le cas échéant)"));
            validity.addCell(getValidityCell(" * La garantie ne doit être réclamée qu'auprès du fabricant respectif"));
            PdfPCell summaryL = new PdfPCell (validity);
            summaryL.setColspan (3);
            summaryL.setPadding (1.0f);
            billTable.addCell(summaryL);

            PdfPTable accounts = new PdfPTable(2);
            accounts.setWidthPercentage(100);
            accounts.addCell(getAccountsCell("subtotal"));
            accounts.addCell(getAccountsCellR(String.valueOf(totalPrix)));

            double promotion = totalPrix.get() * 0.1;
            String promotionString = String.format("%.2f", promotion);


            accounts.addCell(getAccountsCell("Promotion (10%)"));
            accounts.addCell(getAccountsCellR(promotionString));
            accounts.addCell(getAccountsCell("Taxe(2.5%)"));

            double taxe = totalPrix.get() * 0.025;
            String taxetString = String.format("%.2f", taxe);

            accounts.addCell(getAccountsCellR(taxetString));
            accounts.addCell(getAccountsCell("Total"));

            double newTotal = (totalPrix.get() - promotion) + taxe;
            String newTotalString = String.format("%.2f", newTotal);

            accounts.addCell(getAccountsCellR(newTotalString));
            PdfPCell summaryR = new PdfPCell (accounts);
            summaryR.setColspan (3);
            billTable.addCell(summaryR);

            PdfPTable describer = new PdfPTable(1);
            describer.setWidthPercentage(100);
            describer.addCell(getdescCell(" "));
            describer.addCell(getdescCell("Les marchandises une fois vendues ne seront ni reprises ni échangées || Sous réserve de justification du produit || Dommages au produit, personne n'est responsable || "
                    + " Service uniquement dans les centres de service agréés concernés"));

            document.open();

            document.add(image);
            document.add(irhTable);
            document.add(bill);
            document.add(name);
            document.add(contact);
            document.add(address);
            document.add(billTable);
            document.add(describer);

            document.close();

            file.close();

            System.out.println("Pdf created successfully..");
            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        /*	font.setColor(BaseColor.GRAY);*/
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph(text));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    public static PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        return cell;
    }

    public static PdfPCell getBillRowCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getBillFooterCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell getValidityCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorder(0);
        return cell;
    }

    public static PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding (5.0f);
        return cell;
    }
    public static PdfPCell getAccountsCellR(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
        cell.setPadding (5.0f);
        cell.setPaddingRight(20.0f);
        return cell;
    }

    public static PdfPCell getdescCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setBorder(0);
        return cell;
    }

    @Override
    public PageRequestData<Facture2DTO> getAllFacturesPaginated(PageRequest pageRequest) {
        Page<Facture> facturePage = factureRepository.findAll(pageRequest);
        PageRequestData<Facture2DTO> customPageResponse = new PageRequestData<>();
        customPageResponse.setContent(facturePage.map(facture2Mapper::toDto).getContent());
        customPageResponse.setTotalPages(facturePage.getTotalPages());
        customPageResponse.setTotalElements(facturePage.getTotalElements());
        customPageResponse.setNumber(facturePage.getNumber());
        customPageResponse.setSize(facturePage.getSize());
        logger.info("Fetching All factures of Page N° " + pageRequest.getPageNumber());
        return customPageResponse;
    }
}
