package gov.usdot.cv.service.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;

public class CustomClassLoader extends URLClassLoader {
	
	public CustomClassLoader(String jarName) throws FileNotFoundException, MalformedURLException {
		super(getURLs(jarName), null);
	}

	static private URL[] getURLs(String jarName) throws FileNotFoundException, MalformedURLException {
		ArrayList<String> jars = new ArrayList<String>(4);
		
		String jarPath = null;
		String libDir = getLibDir();
		if ( libDir != null ) {
			jarPath = FileSystemHelper.findFile(libDir, jarName);
			jars.add(jarPath);
			jars.add(libDir);
			jars.add(libDir + "oss-7.0.0.jar");
			jars.add(libDir + "log4j-1.2.16.jar");
		} else {
			jarPath = FileSystemHelper.findFile(jarName);
			jars.add(jarPath);
			libDir = new File(jarPath).getParent();
			if ( !libDir.endsWith("lib") ) 
				libDir += File.separator;
			jars.add(libDir);
		}
		final int jarsSize = jars.size();

		String classpath = System.getProperty("java.class.path");
		String[] classpathEntries = classpath.split(File.pathSeparator);
		
		URL[] urls = new URL[classpathEntries.length + jarsSize];
		
		int i = 0;
		for( String jar : jars)
			urls[i++] = toURL(jar);
		
		for( int j = 0; j < classpathEntries.length; j++ )
			urls[i++] = toURL(classpathEntries[j]);
		
		return urls;
	}
	
	private static String getLibDir() {
		final Class<?> cls = org.apache.commons.lang.StringUtils.class;
		try {
			CodeSource codeSource = cls.getProtectionDomain().getCodeSource();
			File jarFile = new File(codeSource.getLocation().toURI().getPath());
			String libDir = jarFile.getParentFile().getAbsolutePath();
			if ( libDir == null ) 
				return null;
			if ( !libDir.endsWith("lib") ) 
				return null;
			return libDir + File.separator;
		} catch (URISyntaxException ex) {
		}
		return null;
	}
	
	static private URL toURL(String path) throws MalformedURLException {
		return new File(path).toURI().toURL();
	}
}
