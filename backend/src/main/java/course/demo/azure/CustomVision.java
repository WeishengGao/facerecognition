package course.demo.azure;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class CustomVision {
    final static String trainingEndpoint = "https://westus2.api.cognitive.microsoft.com";
    final static String trainingApiKey = "aafbfa5cb45f4f61847fa1acd63854ce";
    static RestTemplate restTemplate = new RestTemplate();
    final static String projectID = "f3789e12-31d1-47ee-9b28-49be065ff663";
    public final static String tagIdSandy = "b8f68b6f-0059-4135-b5c5-94a3a8992c24";
    public final static String tagIdOthers = "87f65deb-86ce-455a-989d-83b984a0757c";

    public static void createproject(String projectName) throws JSONException {
        String url = "{endpoint}/customvision/v3.3/Training/projects?name={name}";

        Map<String, String> params = new HashMap<>();
        params.put("endpoint", trainingEndpoint);
        params.put("name", projectName);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        // System.out.println(builder.buildAndExpand(params).toUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Training-key", trainingApiKey);

        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(builder.buildAndExpand(params).toUri(), request,
                String.class);
        // System.out.println(response.getBody())

        JSONObject jsonObject = new JSONObject(response.getBody());
        System.out.println(jsonObject.getString("id"));
    }
    
    // tag
    public static void createTag(String tagName) throws JSONException {
        String url = "{endpoint}/customvision/v3.3/Training/projects/{projectId}/tags?name={name}";

        Map<String, String> params = new HashMap<>();
        params.put("endpoint", trainingEndpoint);
        params.put("projectId", projectID);
        params.put("name", tagName);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        URI uri = builder.buildAndExpand(params).toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Training-key", trainingApiKey);

        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        System.out.println(jsonObject.getString("id") + ": " + tagName);
    }

    //upload image
    public static void uploadImage(String tagId, String fileName) throws JSONException, IOException {
        String url = "{endpoint}/customvision/v3.3/training/projects/{projectId}/images?tagIds={tagIds}";

        Map<String, String> params = new HashMap<>();
        params.put("endpoint", trainingEndpoint);
        params.put("projectId", projectID);
        params.put("tagIds", tagId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        URI uri = builder.buildAndExpand(params).toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Training-key", trainingApiKey);

        Path path = Paths.get(fileName);
        byte[] imageFile = Files.readAllBytes(path);

        HttpEntity<byte[]> request = new HttpEntity<>(imageFile, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);
        System.out.println(response.getBody());
    }

    public static void uploadImage(String tagId, byte[] fileData) throws JSONException, IOException {
        String url = "{endpoint}/customvision/v3.3/training/projects/{projectId}/images?tagIds={tagIds}";

        Map<String, String> params = new HashMap<>();
        params.put("endpoint", trainingEndpoint);
        params.put("projectId", projectID);
        params.put("tagIds", tagId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        URI uri = builder.buildAndExpand(params).toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Training-key", trainingApiKey);

        HttpEntity<byte[]> request = new HttpEntity<>(fileData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);
        System.out.println(response.getBody());
    }
    
    public static void main(String args[]) throws JSONException, IOException {
        // createproject("facerecognition");
        // createTag("Sandy");
        // createTag("Others");
        // uploadImage(tagIdSandy, "/Users/sandygao/face-recognition/backend/images/1a24d20e-d00a-40a0-90b7-74ee414b1678.png");
    }

    public static ResponseEntity<String> validate(byte[] data) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/octet-stream");
        headers.add("Prediction-Key", "aafbfa5cb45f4f61847fa1acd63854ce");

        HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
        String URL = "https://westus2.api.cognitive.microsoft.com/customvision/v3.0/Prediction/f3789e12-31d1-47ee-9b28-49be065ff663/classify/iterations/Iteration1/image";
        ResponseEntity<String> result = restTemplate.postForEntity(URL, entity, String.class);
        return result;
    }
}
