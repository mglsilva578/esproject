package pt.tecnico.myDrive.presentation;
import java.io.*;
import java.util.*;

public class Sys{ 
  private static PrintWriter out = new PrintWriter(System.out, true);
  public static void output(PrintWriter pw) { out = pw; }

  public static void main(String[] args) throws IOException {
    String input;
    Thread master = Thread.currentThread();
    Scanner scan = new Scanner(System.in);

    ProcessBuilder builder;
    if (args.length == 0) builder = new ProcessBuilder("/bin/bash");
    else {
      java.util.List<String> l = new ArrayList<String>();
      for (String s: args) l.add(s);
      builder = new ProcessBuilder(l);
    }
    builder.redirectErrorStream(true);
    Process proc = builder.start();
    OutputStream stdin = proc.getOutputStream ();
    InputStream stdout = proc.getInputStream ();

    BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

    Thread throut = new Thread(new Runnable() {
      @Override
      public void run() {
	String line;
	try {
	  while ((line = reader.readLine ()) != null) {
	    out.println ("Stdout: " + line);
	  }
	} catch (IOException e) { e.printStackTrace(); }
	System.err.println ("Stdout is now closed!!!");
      }
    } );
    throut.start();

    for (;;) {
      do
	try { Thread.sleep(100);
	} catch (InterruptedException e) { }
      while(proc.isAlive() && !scan.hasNext());
      if (proc.isAlive()) {
	if ((input = scan.nextLine()) != null) {
	  writer.write(input);
	  writer.newLine();
	  writer.flush();
	}
      } else break;
    }
    
    try { proc.waitFor();
    } catch (InterruptedException e) { }

    System.err.println ("exit: " + proc.exitValue());
    proc.destroy();
  }
}
