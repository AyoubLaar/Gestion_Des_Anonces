package PFE.Gestion_Des_Anonces.Api.Services;


import PFE.Gestion_Des_Anonces.Api.Models.Anonce.Anonce;
import PFE.Gestion_Des_Anonces.Api.Models.Anonce.AnonceRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.Categorie;
import PFE.Gestion_Des_Anonces.Api.Models.Categorie.CategorieRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Evaluation.Evaluation;
import PFE.Gestion_Des_Anonces.Api.Models.Region.Region;
import PFE.Gestion_Des_Anonces.Api.Models.Region.RegionRepository;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.Ville;
import PFE.Gestion_Des_Anonces.Api.Models.Ville.VilleRepository;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.ANONCE_DTO_HUB;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.ANONCE_DTO_SEARCH;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.COMMENTAIRE_DTO;
import PFE.Gestion_Des_Anonces.Api.utils.DTO_CLASSES.USER_COMMENT_DTO;
import PFE.Gestion_Des_Anonces.Api.utils.SearchFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {

    @Autowired
    private final AnonceRepository anonceRepository;

    @Autowired
    private final VilleRepository villeRepository;
    @Autowired
    private final RegionRepository regionRepository;

    @Autowired
    private final CategorieRepository categorieRepository;
    
    public List<ANONCE_DTO_SEARCH> filterSearch(SearchFilter filter) {
        List<Anonce> anonces;
        if(filter.getType() == -1)
            anonces=anonceRepository.getWithFilterNoCategoriesNoType(
                filter.getMinPrix(),
                filter.getMaxPrix(),
                filter.getChambres(),
                filter.getSalles(),
                "%"+filter.getVille()+"%"
        );
        else anonces = anonceRepository.getWithFilterNoCategories(
                filter.getMinPrix(),
                filter.getMaxPrix(),
                filter.getChambres(),
                filter.getSalles(),
                "%"+filter.getVille()+"%",
                filter.getType()
        );
        if(filter.getCategories().length != 0){
            List<String> FilterCategories = Arrays.asList(filter.getCategories());
            List<Anonce> FilteredAnonces = new ArrayList<>();
            for(Anonce anonce : anonces){
                if(anonce.getCategories().size() >= FilterCategories.size()) {
                    int i=0;
                    int count=0;
                    while( i < anonce.getCategories().size() && count != FilterCategories.size()) {
                        if (FilterCategories.contains(anonce.getCategories().get(i).getIdCategorie()))count++;
                        i++;
                    }
                    if(count == FilterCategories.size())FilteredAnonces.add(anonce);
                }
            }
            anonces = FilteredAnonces;
        }
        return  anonces.stream().map(anonce -> new ANONCE_DTO_SEARCH(
                anonce.getIdAnonce(),
                anonceRepository.getStars(anonce.getIdAnonce()),
                anonce.getPrix(),
                anonce.getLatitude(),
                anonce.getLongitude(),
                anonce.getType(),
                anonce.getEnabled(),
                anonce.getImageUrl(),
                anonce.getNomAnonce(),
                anonce.getIdVille().getIdVille(),
                anonce.getIdVille().getIdRegion().getIdRegion(),
                anonce.getStatus()
        )).toList();
    }


    public List<ANONCE_DTO_SEARCH> getAll() {
        List<Anonce> anonces = anonceRepository.findAll().stream().filter(Anonce::getEnabled).toList();
        return anonces.stream().map(anonce -> new ANONCE_DTO_SEARCH(
                anonce.getIdAnonce(),
                anonceRepository.getStars(anonce.getIdAnonce()),
                anonce.getPrix(),
                anonce.getLatitude(),
                anonce.getLongitude(),
                anonce.getType(),
                anonce.getEnabled(),
                anonce.getImageUrl(),
                anonce.getNomAnonce(),
                anonce.getIdVille().getIdVille(),
                anonce.getIdVille().getIdRegion().getIdRegion(),
                anonce.getStatus()
        )).toList();
    }

    public List<String> getVilles() {
        List<Ville> villes = villeRepository.findAll();
        return villes.stream().map(Ville::getIdVille).toList();
    }

    public List<String> getCategories() {
        List<Categorie> categories = categorieRepository.findAll();
        return  categories.stream().map(Categorie::getIdCategorie).toList();
    }

    public ResponseEntity<?> getAnonce(Long id) {
        Optional<Anonce> anonce = anonceRepository.findById(id);
        if(anonce.isEmpty() || !anonce.get().getEnabled()){
            return ResponseEntity.badRequest().build();
        }
        Anonce A = anonce.get();
        List<COMMENTAIRE_DTO> comments = A.getCommentaires().stream().map(
                commentaire -> new COMMENTAIRE_DTO(
                        new USER_COMMENT_DTO(commentaire.getIdMembre().getNom(),commentaire.getIdMembre().getPrenom())
                        , commentaire.getDatePublication()
                        , commentaire.getContenu()
                )
        ).toList();
        return ResponseEntity.ok().body(
                new ANONCE_DTO_HUB(
                    A.getNomAnonce(),
                    anonceRepository.getStars(A.getIdAnonce()),
                    A.getSurface(),
                    A.getNbreSalleBain(),
                    A.getNbreChambres(),
                    A.getNbreEtages(),
                    A.getPrix(),
                    A.getImageUrl(),
                    A.getDescription(),
                    A.getEmail(),
                    A.getTelephone(),
                    A.getIdVille().getIdVille(),
                    A.getIdVille().getIdRegion().getIdRegion(),
                    A.getType(),
                    comments
                )
        );
    }

    public List<String> getregions() {
        List<Region> regions = regionRepository.findAll();
        return regions.stream().map(Region::getIdRegion).toList();
    }
    public ResponseEntity<?> getEvaluations(Long id){
        Optional<Anonce> anonceOptional = anonceRepository.findById(id);
        if(anonceOptional.isEmpty())return ResponseEntity.badRequest().build();
        Anonce anonce = anonceOptional.get();
        List<Evaluation> evaluations = anonce.getEvaluations();
        List<Map<String,Object>> evaluationsDto = new ArrayList<>();
        for(Evaluation evaluation : evaluations){
            Map<String , Object> x = new HashMap<>();
            x.put("nbretoiles",evaluation.getNbretoiles());
            x.put("contenu",evaluation.getContenu());
            x.put("date",evaluation.getDatePublication());
            x.put("nom",evaluation.getIdMembre().getNom());
            x.put("prenom",evaluation.getIdMembre().getPrenom());
            evaluationsDto.add(x);
        }
        return ResponseEntity.ok(evaluationsDto);
    }

}

