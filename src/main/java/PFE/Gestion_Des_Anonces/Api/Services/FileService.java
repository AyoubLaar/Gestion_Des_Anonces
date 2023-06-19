package PFE.Gestion_Des_Anonces.Api.Services;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    @Autowired
    private final Cloudinary cloudinary;

    public ResponseEntity<?> deleteImage(String publicId){
        try {
            cloudinary.uploader().destroy(publicId,null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
