package pt.tecnico.myDrive.domain;

public class AppMethods {
	public static void main(String... args){
		System.out.println("Welcome to the App default application");
	}
	public static void pdfViewer(String... args){
		for(String s : args)
			System.out.println("pdfViewer: " + s);
	}
	public static void notePad(String... args){
		for(String s : args)
			System.out.println("notePad: " + s);
	}
}
