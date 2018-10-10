/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hpexstream.clients.ews;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.xml.ws.soap.MTOMFeature;

/**
 *
 * @author zywien
 */
public class ServiceClient
{
    private PubFiles PubFiles;
    private String DriverFileName;
    private String MessageFile;
    private String clientOutputDir;
    private boolean ReturnMeta;
    private boolean ReturnMessageFile;
    private boolean SendAuthHeader;
    private boolean UserOutputFile;
    private boolean UseMTOM;
    private Output UserOutput;
    private byte[] Driver;
    private List<Switch> EngineOptions;
    private String SvcUrl;
    private String FileReturnRegEx;
    private String encoding;

    public byte[] getDriver()
    {
        return Driver;
    }

    public void setDriver(byte[] Driver)
    {
        this.Driver = Driver;
    }

    public String getDriverFileName()
    {
        return DriverFileName;
    }

    public void setDriverFileName(String DriverFileName)
    {
        this.DriverFileName = DriverFileName;
    }

    public List<Switch> getEngineOptions()
    {
        return EngineOptions;
    }

    public void setEngineOptions(List<Switch> EngineOptions)
    {
        this.EngineOptions = EngineOptions;
    }

    public String getMessageFile()
    {
        return MessageFile;
    }

    public void setMessageFile(String MessageFile)
    {
        this.MessageFile = MessageFile;
    }

    public PubFiles getPubFile()
    {
        return PubFiles;
    }

    public void setPubFile(PubFiles PubFiles)
    {
        this.PubFiles = PubFiles;
    }

    public Output getRemoteOutputFilename()
    {
        return UserOutput;
    }

    public void setRemoteOutputFilename(String strDir, String strFile)
    {
        UserOutputFile = true;
        this.UserOutput = new Output();
        if(strDir == null) strDir = "";
        this.UserOutput.setDirectory(strDir);

        if(strFile == null) strFile = "";
        this.UserOutput.setFileName(strFile);
    }

    public boolean isReturnMessageFile()
    {
        return ReturnMessageFile;
    }

    public void setReturnMessageFile(boolean ReturnMessageFile)
    {
        this.ReturnMessageFile = ReturnMessageFile;
    }

    public boolean isReturnMeta()
    {
        return ReturnMeta;
    }

    public void setReturnMeta(boolean ReturnMeta)
    {
        this.ReturnMeta = ReturnMeta;
    }

    public boolean isSendAuthHeader()
    {
        return SendAuthHeader;
    }

    public void setSendAuthHeader(boolean SendAuthHeader)
    {
        this.SendAuthHeader = SendAuthHeader;
    }

    public String getClientOutputDir()
    {
        return clientOutputDir;
    }

    public void setClientOutputDir(String clientOutputDir)
    {
        if(clientOutputDir == null)
            this.clientOutputDir = "";
        else
            this.clientOutputDir = clientOutputDir;
    }

    public String getSvcUrl()
    {
        return SvcUrl;
    }

    public void setSvcUrl(String SvcUrl)
    {
        this.SvcUrl = SvcUrl;
    }

    public String getFileReturnRegEx()
    {
        return FileReturnRegEx;
    }

    public void setFileReturnRegEx(String fileReturnRegEx)
    {
        this.FileReturnRegEx = fileReturnRegEx;
    }

    public String getDriverEncoding()
    {
        return encoding;
    }

    public void setDriverEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public void setUseMTOM(boolean UseMTOM) {
        this.UseMTOM = UseMTOM;
    }

    public void Invoke()
    {
        EwsComposeRequest request = new EwsComposeRequest();
        
        DriverFile reqDriver = new DriverFile();
        reqDriver.setDriver(getDriver());

        if(clientOutputDir == null)
            clientOutputDir = "";

        if(DriverFileName == null || DriverFileName.isEmpty())
            DriverFileName = "INPUT";

        if(encoding !=null)
            request.setDriverEncoding(encoding);

        reqDriver.setFileName(DriverFileName);
        request.setDriver(reqDriver);

        if(UserOutputFile)
            request.setOutputFile(UserOutput);

        if(FileReturnRegEx != null && !FileReturnRegEx.isEmpty())
            request.setFileReturnRegEx(FileReturnRegEx);

        // getEngineOptions is a reference to the actual list, not a copy
        request.getEngineOptions().clear();
        Iterator optionIter = getEngineOptions().iterator();
        while(optionIter.hasNext())
        {
            Switch option = (Switch)optionIter.next();
            EngineOption eOpt = new EngineOption();
            eOpt.name = option.Name;
            eOpt.value = option.Value;
            request.getEngineOptions().add(eOpt);
        }

        request.setPubFiles(PubFiles);
        request.setIncludeHeader(true);
        request.setIncludeMessageFile(true);

        try
        {
            URL svcUrl = new URL(SvcUrl);
            EngineService engineService = new EngineService(svcUrl);
            EngineWebService engine = (UseMTOM) ? engineService.getEngineServicePort(new MTOMFeature()) : engineService.getEngineServicePort();

            EwsComposeResponse response = engine.compose(request);

            System.out.println("A response was received...");
            if(response.getFiles().size() > 0)
                System.out.println("Number of output files: " + response.getFiles().size());
            else
                System.out.println("There were no output files.");

            long ticks = System.currentTimeMillis();

            if(response.getEngineMessage() != null)
            {
                System.out.println("Message Content Length: " + response.getEngineMessage().length);
                String msgFileName = clientOutputDir + Long.toString(ticks) + "-Message.txt";
                writeBytesToFile(msgFileName, response.getEngineMessage());
                this.setMessageFile(new String(response.getEngineMessage(),"ISO8859-1"));
            }

            EngineOutput output;
            Header outHeader;
            byte[] outBuffer;
            String outName;
            String outExtension;
            Iterator fileIter = response.getFiles().iterator();
            for(int i = 1; fileIter.hasNext(); i++)
            {
                output = (EngineOutput)fileIter.next();
                outHeader = output.getFileHeader();
                outBuffer = output.getFileOutput();
                outName = output.getFileName();
                outExtension = "out";

                System.out.println("File #" + i + " information");
                if(outHeader != null)
                {
                    System.out.println("Version: " + outHeader.getVersion());
                    System.out.println("FileType: " + outHeader.getFileType());
                    System.out.println("Extension: " + outHeader.getDefaultExtension());
                    outExtension = outHeader.getDefaultExtension();
                    System.out.println("MessageLength: " + outHeader.getMessageLength());
                    System.out.println("OutputLength: " + outHeader.getOutputLength());
                    System.out.println("PageCount: " + outHeader.getPageCount());
                    System.out.println("PDL: " + outHeader.getPDL());
                    System.out.println("ReturnCode: " + outHeader.getReturnCode());
                    System.out.println("UserDataLength: " + outHeader.getUserDataLength());
                    System.out.println("UserData: " + outHeader.getUserData());
                    System.out.println("");
                }

                if(outBuffer != null && outName != null)
                {
                    System.out.println("Content Length: " + outBuffer.length);
                    String outFileName = clientOutputDir + Long.toString(System.currentTimeMillis()) + "-" + outName;
                    writeBytesToFile(outFileName, outBuffer);
                }
                else if (outBuffer != null) 
                {
                    System.out.println("Content Length: " + outBuffer.length);
                    String outFileName = clientOutputDir + Long.toString(System.currentTimeMillis()) + "-OUTPUT_"+ i + "." + outExtension;
                    writeBytesToFile(outFileName, outBuffer);
                }

                if(UserOutputFile && outName != null)
                {
                    System.out.println("User Output File: " + outName);
                }


            }
        }
        catch(EngineServiceException_Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void writeBytesToFile(String fileName, byte[] bytes)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(bytes);
            fos.flush();
            fos.close();
        }
        catch(FileNotFoundException fnfex)
        {
            System.out.println(fnfex.getMessage());
        }
        catch(IOException ioex)
        {
            System.out.println(ioex.getMessage());
        }
    }
}
