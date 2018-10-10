# batch_process

- transformer le fichier de donnée .json en .xml
- Avec les annotations JAXB ont ont va crée un Datafile.xml qui sera utiliser comme fichier d'entrée pour le progiciel EXTREAM OEPN TEXT
- Avec un controler ont envoi des requettes à EXTREAM via le web service SOAP(EngineService) configurer auparavant 
- le moteur Dialoguer de EXTREAM va traiter les données issu du DataFile et nous retourner une requette reponse SOAP(EwsComposeResponse) sous format d'un fichier .pdf.

- REMARQUE : il faut importer la librairie Exstream "EngineClient.jar" pour pouvoir utiliser le programme 
  dans le réperoire EngineClient vous trouverez le code source a deployer sur eclipse pour génerer le .jar
