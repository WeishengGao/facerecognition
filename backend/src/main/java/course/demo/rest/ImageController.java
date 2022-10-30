package course.demo.rest;

import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import course.demo.azure.CustomVision;

// import com.azure.identity.*;
import com.azure.storage.blob.*;
// import com.azure.storage.blob.models.*;
import java.io.*;

@RestController
@CrossOrigin(origins = "${FRONTEND_HOST:*}")
public class ImageController {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hello World";
    }

    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public ResponseEntity<Object> upload(@RequestBody String data) throws IOException, JSONException{
        String base64 = data.replace("data:image/png;base64,", "");
        byte[] decode = Base64.getDecoder().decode(base64);
        // String imageName = UUID.randomUUID() + ".png";
        // saveImageToFile(decode, imageName);
        // saveToCloud(decode, imageName);
        CustomVision.uploadImage(CustomVision.tagIdSandy, decode);
        return new ResponseEntity<Object>("image saved sucessfully", HttpStatus.OK);        
    }

    private void saveImageToFile(byte[] image, String imageName) throws IOException {
        if (!(new File("./images/").exists())) {
            new File("./images/").mkdir();
        }
        Files.write(new File("./images/" + imageName).toPath(), image);   
    }

    private void saveToCloud(byte[] image, String imageName){
        // Retrieve the connection string for use with the application. 
        String connectStr = "DefaultEndpointsProtocol=https;AccountName=facerecognitionstorage;AccountKey=OjoUgmU2RbHPnmI023FYyhuT9MTv+3bZdb6qefcBlFOGiaBzLNKNo1kMELUBWWoWE45dVTAc7Kv1+AStb2gCEg==;EndpointSuffix=core.windows.net";
        // Create a BlobServiceClient object using a connection string
        BlobServiceClient client = new BlobServiceClientBuilder()
            .connectionString(connectStr)
            .buildClient();
        
        // Create a unique name for the container
        String containerName = "images";

        // Create the container and return a container client object
        BlobContainerClient blobContainerClient = client.createBlobContainerIfNotExists(containerName);

        // Get a reference to a blob
        BlobClient blobClient = blobContainerClient.getBlobClient(imageName);

        System.out.println("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());

        // Upload the blob
        // blobClient.uploadFromFile(localPath + fileName);
        InputStream targetStream = new ByteArrayInputStream(image);
        blobClient.upload(targetStream);
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ResponseEntity<String> validate(@RequestBody String data) throws IOException, JSONException {
        String base64 = data.replace("data:image/png;base64,", "");
        byte[] decode = Base64.getDecoder().decode(base64);
        return CustomVision.validate(decode);
    }
}


