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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		this.getServletContext().getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
		throw new IOException("GET GET GUT");
	}

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

//SOLLUTION 1
		// l'objet data est utiliser pour crée un fichier PDF "test.xml et pour le
		// convertir en bite
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
		 * 2.1--convertir un fihcier XML en Bite pour etre utiliser dans le setDriver de
		 * a requete SOAP
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

		/**
		 * les paramettre de chaque setter se trouve dans le fichier ews-config.xml dans
		 * (C:\Users\Badr\Desktop\apache-tomcat-8.5.33\webapps\EngineService\WEB-INF)
		 */

		EngineOption eOption = new EngineOption();
		eOption.setName("RUNMODE");
		eOption.setValue("PRODUCTION");
		wsr.getEngineOptions().add(eOption);

		EngineOption eOption1 = new EngineOption();
		eOption1.setName("FILEMAP");
		eOption1.setValue("REF_Agences,C:\\Users\\Badr\\Desktop\\EWS\\REF_Agences.csv");
		wsr.getEngineOptions().add(eOption1);

		// ont utilise eOption2 pour stocker les fichier de sortie mais la page ne sera
		// pas afficher dans le navigateur web si cette variable est utiliser
//		EngineOption eOption2 = new EngineOption();
//		eOption2.setName("OUTPUTDIRECTORY");
//		eOption2.setValue("C:\\Users\\Badr\\Desktop\\EWS\\output\\");
//		wsr.getEngineOptions().add(eOption2);

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

//SOLLUTION 2

//		byte[] table ; 
//		try {
//			
//			JAXBContext jaxbContext = JAXBContext.newInstance(DataFile.class);
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//			
//			File dataFile = new File("C:\\Users\\Nadir Boutra\\Desktop\\CustomData.xml");
//			StringWriter data = new StringWriter() ; 
//			StringBuffer buffer = new StringBuffer() ;
//			
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			jaxbMarshaller.marshal(datas, dataFile);
//			jaxbMarshaller.marshal(datas, data);
//			buffer = data.getBuffer();
//			table = buffer.toString().getBytes() ;
//			
//		} catch (JAXBException e) {
//          e.printStackTrace();
//          throw new IOException("erreur d'ecriture xml") ;
//		}
//		
//		DriverFile df = new DriverFile();
//		df.setFileName("FILEINPUT");
//		df.setDriver(table);
//	
//		
//		EwsComposeRequest req = new EwsComposeRequest() ;
//		req.setDriver(df);  // A completer 
////		req.setDriverEncoding("");         // A completer 
//		                                  // Engine Options
//		                                  // fileReturnRegEx
//		req.setIncludeHeader(true);      // A verifier
//		req.setIncludeMessageFile(true);  // true 
//		
////        Output o = new Output() ; 
////		Output o1 = new Output() ; 
////		
////		o1.setFileName("o_afp");
////		o.setFileName("o_pdf");
////		o.setDirectory("C:\\Users\\Nadir Boutra\\Desktop\\EWS\\Output");
////		o1.setDirectory("C:\\Users\\Nadir Boutra\\Desktop\\EWS\\Output");
////		req.setOutputFile(new Output());  // A completer
////		req.setOutputFile(o1);  // A completer
////		req.setOutputFile(o);  // A completer
//		
//		req.setPubFile("Communication1.pub");               // A completer
//		EngineOption eOption = new EngineOption() ;
//		eOption.setName("RUNMODE");
//		eOption.setValue("PRODUCTION");
//		EngineOption eOption1 = new EngineOption() ;
//		eOption1.setName("FILEMAP");
//		eOption1.setValue("REF_DBAgence,C:\\Users\\Nadir Boutra\\Desktop\\formation\\REF_DBAgence.csv");
//		req.getEngineOptions().add(eOption);
//		req.getEngineOptions().add(eOption1);
//		
//		EngineService es = new EngineService(new URL("http://localhost:8080/EngineService/EngineService?wsdl"));
//		EwsComposeResponse res ;
//		try {
//			res = es.getEngineServicePort().compose(req);
//			System.out.println(new String (res.getEngineMessage()));
//			System.out.println(res.getFiles().size());
//			PrintWriter output = response.getWriter();
//			response.setContentType("application/pdf");
//			output.print(new String (res.getFiles().get(1).getFileOutput() ));
//		} catch (EngineServiceException_Exception e) {
//			
//			e.printStackTrace();
//			throw new IOException("erreur d'appel a EWS") ;
//		}

		/**
		 * instruction pour le redirect (dnas notre cas ont a pas besoin d'un redirect
		 */
		// response.sendRedirect(this.getServletContext().getContextPath() + "/client");
	}

}

