package PFE.Gestion_Des_Anonces.Api.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SearchFilter {
    private float minPrix;
    private float maxPrix;
    private float chambres;
    private float salles;
    private String ville;
    private String [] categories;
    private int Type;
}
