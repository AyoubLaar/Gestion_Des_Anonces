package PFE.Gestion_Des_Anonces.Api.Models.Anonce;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnonceRepository extends JpaRepository<Anonce,Long> {

    @Query(value="SELECT distinct a.* FROM anonce a " +
            "WHERE a.prix >= :minPrix " +
            "AND a.prix <= :maxPrix " +
            "AND a.nbre_chambres >= :chambres " +
            "AND a.nbre_salle_bain >= :salles " +
            "AND LOWER(a.id_ville) like LOWER(:ville) " +
            "AND a.status = 0 "+
            "And a.type = :type"
            , nativeQuery = true
    )
    List<Anonce> getWithFilterNoCategories(float minPrix,
                               float maxPrix,
                               float chambres,
                               float salles,
                               String ville,
                               int type);

    @Query(value="SELECT COALESCE(AVG(nbretoiles),0) FROM evaluation " +
            "WHERE id_anonce = :id "
            , nativeQuery = true
    )
    Float getStars(long id);
    @Query(value="SELECT distinct a.* FROM anonce a " +
            "WHERE a.prix >= :minPrix " +
            "AND a.prix <= :maxPrix " +
            "AND a.nbre_chambres >= :chambres " +
            "AND a.nbre_salle_bain >= :salles " +
            "AND LOWER(a.id_ville) like LOWER(:ville) " +
            "AND a.status = 0 "
            , nativeQuery = true
    )
    List<Anonce> getWithFilterNoCategoriesNoType(float minPrix, float maxPrix, float chambres, float salles, String ville);
}

   /* SELECT a FROM anonce a JOIN categories_anonces c ON a.id_anonce = c.id_anonce WHERE a.prix > :minPrix AND a.prix < :maxPrix AND a.nbre_chambres >= :chambres AND a.nbre_salle_bain >= :salles AND a.id_ville like :ville AND c.id_categorie IN :categories;
*/