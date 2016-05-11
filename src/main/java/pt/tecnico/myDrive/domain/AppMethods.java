package pt.tecnico.myDrive.domain;

public class AppMethods {
	public static void main(String... args){
		System.out.println("Welcome to the App default application");
	}
	public static void pdfViewer(String... args){
		System.out.println("pdfViewer");
		for(String s : args)
			System.out.println(s);
	}
	public static void notePad(String... args){
		System.out.println("NotePad");
		for(String s : args)
			System.out.println(s);
	}
}
