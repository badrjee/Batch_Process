package fr.meg.editique.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlType(propOrder = {"soldeActuel","soldeAncien"})
public class Entree {
	
	private String type ;
	private double montant ; 
	private String date ; 
	private String desc ;
	private double soldeAncien ; 
	private double soldeActuel ;
	
	
	public String getType() {
		return type;
	}
	
	@XmlAttribute
	public void setType(String type) {
		this.type = type;
	}
	public double getMontant() {
		return montant;
	}
	
	@XmlAttribute
	public void setMontant(double montant) {
		this.montant = montant;
	}
	public String getDate() {
		return date;
	}
	
	@XmlAttribute
	public void setDate(String date) {
		this.date = date;
	}
	public String getDesc() {
		return desc;
	}
	
	@XmlAttribute
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public double getSoldeAncien() {
		return soldeAncien;
	}
	
	@XmlElement
	public void setSoldeAncien(double soldeAncien) {
		this.soldeAncien = soldeAncien;
	}
	public double getSoldeActuel() {
		return soldeActuel;
	}
	
	@XmlElement
	public void setSoldeActuel(double soldeActuel) {
		this.soldeActuel = soldeActuel;
	} 
	
	

}
