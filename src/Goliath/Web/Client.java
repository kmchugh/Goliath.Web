package Goliath.Web;

import Goliath.Exceptions.InvalidIOException;
import Goliath.Interfaces.Web.IClientBehaviour;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Client extends Goliath.Object implements IClientBehaviour {

	/**
	 * Absolute location on the file system of root directory of the cache
	 */
	private String m_cCacheDirectory = "/tmp/";
	// TODO: have this local cache configurable via the the application settings manager

	/**
	 * Returns a String object which contains the path to a image file (.jpg) on the 
	 * file system. This class assumes that there is a directory where it is possible to store these resources.
	 * This method returns immediately if the url has been previously accessed and is cached.
	 * @param tcUrl
	 * @return the path to the image file (.jpg)
	 */
	public String grabSite(String tcUrl) {
		System.out.println(tcUrl + "\n"); 
		  try{
		   String encodedurl = URLEncoder.encode(tcUrl.toString(),"UTF-8"); 
		  
		  }catch(UnsupportedEncodingException ex){
                       Goliath.Applications.Application.getInstance().log(ex);
		   return null;
		  }
		
		try {
			

			URL loUrl = null;
			try {
				// TODO: have this configurable by the user, also we need to discover the service rather than
				// harcoding the url
				loUrl = new java.net.URL("http://dbe.dnsalias.net:2727/CognitiveEdge" +
						"?url=" + tcUrl + "&outputType=string&refresh=false");
			} catch (MalformedURLException ex) {
				 Goliath.Applications.Application.getInstance().log(ex);
				return null;
			}

			// We check if our directory has the "/" (unix). If not we add it
			if (!m_cCacheDirectory.endsWith(File.separator)) {
				m_cCacheDirectory = m_cCacheDirectory + File.separator;
			}

			// Obtain the hashcode. We will store the local cache copy with the format
			// cacheDirectory/ce-hascode/
			int loUrlHashcode = loUrl.hashCode();

			m_cCacheDirectory = m_cCacheDirectory + "ce" + Integer.toString(loUrlHashcode) + File.separator;
			File loDirectory = new File(m_cCacheDirectory);

			// Does the directory exist already in our local cache?. We need to create it
			if (!loDirectory.exists()) {

				boolean llSucess = loDirectory.mkdirs();
				// TODO: do something if the directory/ies could not be created
			}

			// We call the webservice that Scraps the url. This webservice returns
			// another url that will contain the location of the results of the scrapping
			String lcContent = executeWebService(loUrl);

			// We check if we got a result
			// TODO: Check that the result is actually valid (i.e. url exist, resource exist)
			if (lcContent == null)
			{
				return null;

			}


			try {
				loUrl = new java.net.URL(lcContent + "content.zip");
			} catch (MalformedURLException ex) {
				 Goliath.Applications.Application.getInstance().log(ex);
				return null;
			}


			// We now download the zip file and expand it in our cache
			downloadBinaryFileFromUrl(loUrl, m_cCacheDirectory);
			expandZipFile(m_cCacheDirectory, "content.zip");

			// We are just returning the medium size jpeg (medium.jpg). 
			// TODO: this should be configurable
			return m_cCacheDirectory + "images" + File.separator + "medium.jpg";
		} catch (InvalidIOException ex) {
			 Goliath.Applications.Application.getInstance().log(ex);
			return null;
		}



	}

	/**
	 * Executes our "REST" like Grabber service
	 * @param toUrl
	 * @return
	 */
	private String executeWebService(URL toUrl) {


		

		try {

			// We need to first encode the URL just in case it contains special chars (?,&, etc)

			// We try to connect to the URL
			HttpURLConnection loConnection = (HttpURLConnection) toUrl.openConnection();
			loConnection.connect();

			// 	However several errors can happen
			if (loConnection.getResponseCode() / 100 != 2) {

				// TODO: Handler this error, there was something wrong with the resource we were trying to access (might be it is not there anymore)
			}

			// Check for valid content length.
			int lnContentLength = loConnection.getContentLength();
			if (lnContentLength < 1) {// Make sure response code is in the 200 range.
				// TODO: Handle this error, there was something wrong with the resource itself (faulty file?)

			}


			// It seems we have a valid file, we try to download it and save it to the local cache/file
			// TODO: The previous code is similar to the one on the next methond (connectig to webserver). Need to refactor it into a IO web class
			InputStream loIn = loConnection.getInputStream();

			String lcLine = "";
			String lcOutput = "";

			InputStreamReader inR = new InputStreamReader(loIn);
			BufferedReader loBuf = new BufferedReader(inR);

			while ((lcLine = loBuf.readLine()) != null) {
				lcOutput = lcOutput + lcLine;
			}


			return lcOutput;

		} catch (Exception ex) {
			 Goliath.Applications.Application.getInstance().log(ex);
			return null;
		}
	}

	/**
	 * Downloads the specified {@link URL} to the tcDirectory   
	 * @param toUrl
	 * @param tcDirectory
	 * @throws Goliath.Exceptions.InvalidIOException
	 */
	private void downloadBinaryFileFromUrl(URL toUrl, String tcDirectory) 
	throws Goliath.Exceptions.InvalidIOException {
		try {

			HttpURLConnection loConnection = (HttpURLConnection) toUrl.openConnection();

			// We try to connect to the URL. However several errors can happen
			loConnection.connect();
			if (loConnection.getResponseCode() / 100 != 2) {
				// TODO: Handler this error, there was something wrong with the resource we were trying to access (might be it is not there anymore)
			}

			// Check for valid content length.
			int lnContentLength = loConnection.getContentLength();
			if (lnContentLength < 1) { 

			}
			// It seems we have a valid file, we try to download it and save it to the local cache/file
			File loFile = new File(tcDirectory + getFileName(toUrl));
			InputStream loIn = loConnection.getInputStream();
			FileOutputStream loOut = new FileOutputStream(loFile);
			copyInputStream(loIn, loOut);
		} catch (IOException ex) {
			 Goliath.Applications.Application.getInstance().log(ex);
			throw new Goliath.Exceptions.InvalidIOException("Exception while " +
					"dowloading files  (unzip) " + Client.class.getName(), ex);
		}
	}
	/**
	 * Unzips the tcName compressed zip file which should be located relative to 
	 * tcDirectory. The contents of tcName are extracted relative to tcDirectory. 
	 * @param tcDirectory
	 * @param tcName
	 * @throws Goliath.Exceptions.InvalidIOException
	 */
	private void expandZipFile(String tcDirectory, String tcName)
	throws Goliath.Exceptions.InvalidIOException {
		try {

			ZipFile loZipFile;
			Enumeration<? extends ZipEntry> loZipEntries;
			String lcZipFile = tcDirectory + tcName;
			loZipFile = new ZipFile(lcZipFile);


			loZipEntries = loZipFile.entries();

			// loZipEntries contains the files that form part of this zip file. We iterate over them and expand them relative to tcDirectory
			while (loZipEntries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) loZipEntries.nextElement();

				if (entry.isDirectory()) {
					// We assume that directories are stored parents first. Then
					// chidren
					(new File(tcDirectory + entry.getName())).mkdir();
					continue;
				}
				copyInputStream(loZipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(tcDirectory + entry.getName())));
			}

			loZipFile.close();
			(new File(lcZipFile)).delete();
		} catch (IOException ex) {
			 Goliath.Applications.Application.getInstance().log(ex);
			throw new Goliath.Exceptions.InvalidIOException("Exception while expanding (unzip) files,  " + Client.class.getName(), ex);
		}

	}


	/***
	 * Copies a given {@link InputStream} toIn to an output one toOut. 
	 * @param toIn
	 * @param toOut
	 * @throws Goliath.Exceptions.InvalidIOException
	 */
	private void copyInputStream(InputStream toIn, OutputStream toOut)
	throws Goliath.Exceptions.InvalidIOException{
		try {
			byte[] lbBuffer = new byte[1024];
			int lnLen;

			while ((lnLen = toIn.read(lbBuffer)) >= 0) {
				toOut.write(lbBuffer, 0, lnLen);
			}

			toIn.close();
			toOut.close();
		} catch (IOException ex) {
			 Goliath.Applications.Application.getInstance().log(ex);
			throw new Goliath.Exceptions.InvalidIOException("Exception while " +
					"trying to copy the InputStream, " + Client.class.getName(), ex);
		}
	}

	/**
	 * Extracts the last part of a {@link URL} assuming that toUrl points to a 
	 * file. This method is very simple but we cannot assume that we always can
	 * extract a file name. For example http://www.google.com will not return a 
	 * file name.
	 * @param toUrl
	 * @return the file name extracted
	 */
	private String getFileName(URL toUrl) {
		String lcFileName = toUrl.getFile();
		return lcFileName.substring(lcFileName.lastIndexOf('/') + 1);
	}
}
