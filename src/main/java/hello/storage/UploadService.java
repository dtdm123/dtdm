package hello.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;

@Service
public class UploadService {
private static final String APPLICATION_NAME ="Drive API Quickstart";
	
	private static final File DATA_STORE_DIR = 
			new File(System.getProperty("user.home"),".credentials/drive-java-quickstart");
	
	private static FileDataStoreFactory DATA_STORE_FACTORY;
	
	private static final JsonFactory JSON_FACTORY =
			JacksonFactory.getDefaultInstance();
	
	private static HttpTransport HTTP_TRANSPORT;
	
	private static final List<String> SCOPES = 
			Arrays.asList(DriveScopes.DRIVE_FILE);
	
	static {
		try{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
			
			
		}catch(Throwable t){
			t.printStackTrace();
			System.exit(1);
		}
	}
	
	public static Credential authorize() throws IOException {
		InputStream in =
				UploadService.class.getResourceAsStream("/client_secret.json");
		
		GoogleClientSecrets clientSecrets = 
				GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		
		GoogleAuthorizationCodeFlow flow = 
				new GoogleAuthorizationCodeFlow
				.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline")
				.build();
		Credential credential = 
				new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		
		System.out.println("Credentials saved to " +DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}
	
	public static Drive getDriveService() throws IOException {
		Credential credential = authorize();
		return new Drive.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
		
	}
	
	public void Upload(File filePath) throws IOException {
		Drive service = getDriveService();

		Path rootLocation = null; 
		com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
		fileMetadata.setName("123.jpg");
	
		System.out.println("kt" +  filePath.getAbsolutePath());
		FileContent mediaContent = new FileContent("image/jpeg", filePath.getAbsoluteFile());
		com.google.api.services.drive.model.File file = service.files()
				.create(fileMetadata,mediaContent)
				.setFields("id")
				.execute();
		
		System.out.println("File id: " +file.getId());
	}
}
