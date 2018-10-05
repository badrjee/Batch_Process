package fr.meg.editique.domain;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlType(propOrder = {"agence", "civ", "nom","prenom","adr_11","adr_12","cp","ville","releve"})
public class Client {
	
	
	

	private String nom;
	private String prenom;
	private Integer num ;
	private Integer agence ;
	private String civ ;
	private String adr_11 ; 
	private String adr_12 ;
//	private String adr_13 ;
	private String cp ; 
	private String ville ;
	private List<Entree> releve = new ArrayList<>() ;
	
	
	
	
	public String getCiv() {
		return civ;
	}
	@XmlElement
	public void setCiv(String civ) {
		this.civ = civ;
	}
	public String getAdr_11() {
		return adr_11;
	}
	@XmlElement
	public void setAdr_11(String adr_11) {
		this.adr_11 = adr_11;
	}
	public String getAdr_12() {
		return adr_12;
	}
	@XmlElement
	public void setAdr_12(String adr_12) {
		this.adr_12 = adr_12;
	}
//	public String getAdr_13() {
//		return adr_13;
//	}
//	@XmlElement
//	public void setAdr_13(String adr_13) {
//		this.adr_13 = adr_13;
//	}
	public String getCp() {
		return cp;
	}
	@XmlElement
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getVille() {
		return ville;
	}
	@XmlElement
	public void setVille(String ville) {
		this.ville = ville;
	}
	
	public List<Entree> getReleve() {
		return releve;
	}
	
	@XmlElementWrapper(name = "releve")
	@XmlElement(name="entree")
	public void setReleve(List<Entree> releve) {
		this.releve = releve;
	}
	public String getNom() {
		return nom;
	}
	@XmlElement
	public void setNom(String nom) {
		this.nom = nom;
	}
	
	public String getPrenom() {
		return prenom;
	}
	@XmlElement
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	
	public Integer getNum() {
		return num;
	}
	
	@XmlAttribute
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getAgence() {
		return agence;
	}
	
	@XmlElement
	public void setAgence(Integer agence) {
		this.agence = agence;
	}
	
	
	

}