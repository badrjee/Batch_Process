package fr.meg.editique.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.hpexstream.clients.ews.DriverFile;
import com.hpexstream.clients.ews.EngineOption;
import com.hpexstream.clients.ews.EngineOutput;
import com.hpexstream.clients.ews.EngineService;
import com.hpexstream.clients.ews.EngineServiceException_Exception;
import com.hpexstream.clients.ews.EwsComposeRequest;
import com.hpexstream.clients.ews.EwsComposeResponse;
import com.hpexstream.clients.ews.Output;

import fr.meg.editique.domain.Client;
import fr.meg.editique.domain.DataFile;
import fr.meg.editique.domain.Entree;

public class Servletcontrol extends HttpServlet {

	private static final long serialVersionUID = 1L;

//	@Override
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
////		this.getServletContext().getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
//		throw new IOException("GET GET GUT");
//	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/**
		 * 1--crée un fichier XML en utilisant les Annotation JaxB
		 */
		DataFile datafile = new DataFile();
		Client c = new Client();
		Entree r = new Entree();
		
		c.setAgence(Integer.parseInt(request.getParameter("agence")));
		c.setCiv(request.getParameter("civ"));
		c.setNom(request.getParameter("nom"));
		c.setPrenom(request.getParameter("prenom"));
		c.setNum(Integer.parseInt(request.getParameter("num")));
		c.setAdr_11(request.getParameter("adresse"));
		c.setAdr_12(request.getParameter("adresse2"));
		c.setCp(request.getParameter("cp"));
		c.setVille(request.getParameter("ville"));
		r.setType(request.getParameter("type"));
		r.setDate(request.getParameter("date"));
		r.setDesc(request.getParameter("desc"));
		r.setMontant(Double.parseDouble(request.getParameter("montant")));
		r.setSoldeActuel(Double.parseDouble(request.getParameter("actuelSolde")));
		r.setSoldeAncien(Double.parseDouble(request.getParameter("ancienSolde")));
		
		c.getReleve().add(r);
		
		datafile.setClient(c);
		
		//l'objet data est utiliser pour crée un fichier PDF "test.xml et pour le convertir en bite
		File data = new File("C:\\Users\\Badr Azeri\\Desktop\\EWS\\temp\\test.xml");

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(DataFile.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();	

			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(datafile, data);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		
		
		/**
		 * 2--preparer la requete a envoyer en SOAP:
		 */
		EwsComposeRequest wsr = new EwsComposeRequest();

		/**
		 * 2.1--convertir un fihcier XML en Bite pour etre utiliser dans le setDriver de a requete SOAP
		 */
		// init array with file length
		byte[] bytesArray = new byte[(int) data.length()];

		FileInputStream fis = new FileInputStream(data);
		fis.read(bytesArray); // read file into bytes[]
		fis.close();

		/**
		 * 2.2--parametre du Driver:
		 */
		DriverFile df = new DriverFile();
		df.setFileName("FILEINPUT");
		df.setDriver(bytesArray);

		/**
		 * 2.3--contenu de la requete envoiyer en SOAP qui est des données xml
		 */
		wsr.setIncludeHeader(true);
		wsr.setDriver(df);
		wsr.setIncludeMessageFile(true);
		wsr.setOutputFile(new Output());
		wsr.setPubFile("ExstreamPackage.pub");

		EngineOption eOption = new EngineOption();
		EngineOption eOption1 = new EngineOption();
		EngineOption eOption2 = new EngineOption();
		
		//les paramettre de chaque setter se trouve dans le fichier ews-config.xml dans (C:\Users\Badr Azeri\Desktop\apache-tomcat-8.5.33\webapps\EngineService\WEB-INF)
		eOption.setName("RUNMODE");
		eOption.setValue("PRODUCTION");
		
		eOption1.setName("FILEMAP");
		eOption1.setValue("REF_Agences,C:\\Users\\Badr Azeri\\Desktop\\Formation\\REF_Agences.csv");
		
		eOption2.setName("OUTPUTDIRECTORY");
		eOption2.setValue("C:\\Users\\Badr Azeri\\Desktop\\EWS\\output\\");
		
		wsr.getEngineOptions().add(eOption);
		wsr.getEngineOptions().add(eOption1);
		wsr.getEngineOptions().add(eOption2);

		

		
		
		/**
		 * 3--appel le service web SOAP
		 */
		EngineService es = new EngineService(new URL("http://localhost:8080/EngineService/EngineService?wsdl"));

		
		
		/**
		 * 4--SOAP envoi la reponse en format de fichier pdf "application/pdf"
		 */
		EwsComposeResponse res = new EwsComposeResponse();
		try {
			res = es.getEngineServicePort().compose(wsr);
			System.out.println(new String(res.getEngineMessage()));

			PrintWriter output = response.getWriter();
			// response.setContentType("application/pdf");
			// output.print(new String (res.getFiles().get(1).getFileOutput() ));

			/**
			 * on utilse cette boucle pour afficher que des document PDf AVANT avant il est
			 * necessaire de voir le debogage
			 */
			for (EngineOutput eo : res.getFiles()) {
				if (eo.getFileHeader().getPDL() == 4) {
					response.setContentType("application/pdf");
					output.print(new String(eo.getFileOutput()));
					return;
				}
			}

		} catch (EngineServiceException_Exception e) {

			e.printStackTrace();
			throw new IOException("erreur d'appel a EWS");
		}

		// instruction pour le redirct (dnas notre cas ont a pas besoin d'un rederct
		// response.sendRedirect(this.getServletContext().getContextPath() + "/client");
	}

}
