package fr.meg.editique.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * une classe qui permet de crée une balise Data file qui ressemble au fichier xml traiter par Exstream
 * @author Badr Azeri
 *
 */

@XmlRootElement (name= "data_file")
public class DataFile {
	
	private Client client;

	public Client getClient() {
		return client;
	}

	@XmlElement
	public void setClient(Client client) {
		this.client = client;
	} 

}
