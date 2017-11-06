package gov.usdot.cv.service.rest;

import static org.junit.Assert.*;

import gov.usdot.cv.service.util.CustomClassLoader;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

import org.junit.Test;

import com.oss.util.HexTool;

public class JarClassLoaderTest {
	
	@Test
	public void test() throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException {
		// The test below will fail until references to asn project are not removed from the pom file
		test("fedgov-cv-asn1-1.0.0-SNAPSHOT.jar", "118000000008004f8580", "gov.usdot.asn1.generated.j2735.semi.DataReceipt");
	}
	public void test(String jarName, String message, String msgType) throws MalformedURLException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException {
		CustomClassLoader loader = new CustomClassLoader(jarName);
		assertNotNull(loader);
		Class<?> j2735 = loader.loadClass("gov.usdot.asn1.generated.j2735.J2735");
		assertNotNull(j2735);
		
		// call static method initialize: gov.usdot.asn1.generated.j2735.J2735.initialize();
		Method initializeMethod = j2735.getMethod("initialize");
		assertNotNull(initializeMethod);
		initializeMethod.invoke(null);
		
		Method getPERUnalignedCoderMethod = j2735.getMethod("getPERUnalignedCoder");
		assertNotNull(getPERUnalignedCoderMethod);
		Object coder = getPERUnalignedCoderMethod.invoke(null);
		assertNotNull(coder);
		
		// call instance method enableDecoderDebugging
		Class<?> berCoder = loader.loadClass("com.oss.asn1.Coder");
		assertNotNull(berCoder);
		Method enableDecoderDebuggingMethod = berCoder.getMethod("enableDecoderDebugging");
		assertNotNull(enableDecoderDebuggingMethod);
		enableDecoderDebuggingMethod.invoke(coder);
		
		// call instance method with parameter: com.oss.asn1.Coder.setDecoderDebugOut(PrintWriter arg0)
		Method setDecoderDebugOutMethod = berCoder.getMethod("setDecoderDebugOut", PrintWriter.class);
		assertNotNull(setDecoderDebugOutMethod);
		StringWriter stringWriter = new StringWriter();
		try {
			setDecoderDebugOutMethod.invoke(coder, new PrintWriter(stringWriter));
			
			// decode message: AbstractData com.oss.asn1.Coder.decode(InputStream arg0, AbstractData arg1)
			Class<?> abstractData = loader.loadClass("com.oss.asn1.AbstractData");
			assertNotNull(abstractData);
			Method decodeMethod = berCoder.getMethod("decode", InputStream.class, abstractData);
			assertNotNull(decodeMethod);
			// param 0 InputStream
			byte[] bytes = HexTool.parseHex(message, false);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			// param 1: gov.usdot.asn1.generated.j2735.semi.DataReceipt.DataReceipt()
			Class<?> dataReceipt = loader.loadClass(msgType);
			assertNotNull(dataReceipt);
			Object dataReceiptInstance = dataReceipt.newInstance();
			assertNotNull(dataReceiptInstance);
			Object decodedAbstractData = decodeMethod.invoke(coder, inputStream, dataReceiptInstance);
			assertNotNull(decodedAbstractData);
			System.out.println(decodedAbstractData);
			//if ( false )
			//	throw new DecoderException(null);
			/**/
		} catch ( Exception ex ) {
			Throwable cause = ex.getCause();
			if ( cause != null ) {
				System.out.println(cause.getMessage());
				Throwable cause2 = cause.getCause();
				if ( cause2 != null ) {
					System.out.println(cause2.getMessage());
				}
			}
			System.out.println(ex.getMessage()); /**/
		} finally {
			System.out.println(stringWriter);
		}
	}
}
