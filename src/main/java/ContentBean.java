

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.icefaces.ace.component.fileentry.FileEntry;
import org.icefaces.ace.component.fileentry.FileEntryEvent;
import org.icefaces.ace.component.fileentry.FileEntryResults;

import eu.learnpad.ca.rest.data.QualityCriteria;
import eu.learnpad.ca.rest.data.collaborative.CollaborativeContent;
import eu.learnpad.ca.rest.data.collaborative.CollaborativeContentAnalysis;




@ManagedBean(name="ContentBean")
@SessionScoped
public class ContentBean implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2574895928017048791L;
	private String Title;
	private int id;
	private String Content;
	private String Language;
	private String restid;
	private String filecontent;
	public ContentBean(){

	}


	public String getFilecontent() {
		return filecontent;
	}


	public void setFilecontent(String filecontent) {
		this.filecontent = filecontent;
	}


	public String getRestid() {
		return restid;
	}



	public void setRestid(String restid) {
		this.restid = restid;
	}



	public String getLanguage() {
		return Language;
	}



	public void setLanguage(String language) {
		Language = language;
	}



	public String getTitle() {
		return Title;
	}



	public void setTitle(String title) {
		Title = title;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public String getContent() {
		return Content;
	}



	public void setContent(String content) {
		Content = content;
	}

	public void sampleListener(FileEntryEvent e) {
		FileEntry fe = (FileEntry)e.getComponent();
		FileEntryResults results = fe.getResults();

		for (FileEntryResults.FileInfo fileInfo : results.getFiles()) {
			System.out.println(fileInfo);


			if (fileInfo.isSaved()) {
				// Process the file. Only save cloned copies of results or fileInfo,StandardCharsets.UTF_8

				Path path = Paths.get(fileInfo.getFile().getAbsolutePath());
				try {
					filecontent = 	new String(Files.readAllBytes(path));
					System.out.println(filecontent);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

	public void submitButton(ActionEvent event) {


		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080").path("contentanalysis/learnpad/ca/validatecollaborativecontent");

		CollaborativeContentAnalysis cca = new CollaborativeContentAnalysis();
		cca.setLanguage("english");
		cca.setCollaborativeContent(new CollaborativeContent(String.valueOf(this.getId()), this.getTitle()));
		cca.getCollaborativeContent().setContent(new eu.learnpad.ca.rest.data.Content());
		if(filecontent!=null){
			cca.getCollaborativeContent().getContent().setContent(filecontent);
		}else{
			cca.getCollaborativeContent().getContent().setContent(getContent());
		}
		cca.setQualityCriteria(new QualityCriteria());
		cca.getQualityCriteria().setCorrectness(true);
		cca.getQualityCriteria().setSimplicity(true);
		cca.getQualityCriteria().setContentClarity(false);
		cca.getQualityCriteria().setNonAmbiguity(false);
		cca.getQualityCriteria().setCompleteness(false);
		cca.getQualityCriteria().setPresentationClarity(false);

		Entity<CollaborativeContentAnalysis> entity = Entity.entity(cca,MediaType.APPLICATION_XML);
		//GenericEntity<JAXBElement<CollaborativeContentAnalysis>> gw = new GenericEntity<JAXBElement<CollaborativeContentAnalysis>>(cca){};
		Response response =  target.request(MediaType.APPLICATION_XML).post(entity);

		String id = response.readEntity(String.class);

		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getRequestMap().put("rest", id);



		this.setRestid(id);

		System.out.println("Submit Clicked: " + Title + ", " + Content + ", " + id + "; ");
	}
}
