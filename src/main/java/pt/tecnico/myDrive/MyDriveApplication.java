package pt.tecnico.myDrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.exception.MyDriveException;

public class MyDriveApplication {
    // FenixFramework will try automatic initialization when first accessed
    public static void main(String [] args) {
    	System.out.println("Welcome to MyDrive Application");
        try {
            MyDriveApplication.setup();
        }catch(MyDriveException mde){
        	System.out.println(mde.getMessage());
        }catch(Exception e){
        	System.out.println(e.getMessage());
        }finally {
            // ensure an orderly shutdown
            FenixFramework.shutdown();
        }
    }
    @Atomic
    public static void init(){
    	//TODO clean MyDrive
    }
    
    @Atomic
    public static void setup() {
		MyDrive drive = MyDrive.getInstance();
		System.out.println(drive.toString());
	}

	public static void applicationCodeGoesHere() {
        someTransaction();
    }

    @Atomic
    public static void someTransaction() {
        System.out.println("FenixFramework's root object is: " + FenixFramework.getDomainRoot());
    }
}
