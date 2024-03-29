package pt.tecnico.myDrive.presentation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.domain.LoginManager;
import pt.tecnico.myDrive.domain.MyDrive;
import pt.tecnico.myDrive.domain.Nobody;
import pt.tecnico.myDrive.exception.InvalidUsernameException;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public abstract class Shell {
	
	protected static final Logger log = LogManager.getRootLogger();
	private Map<String,Command> coms = new TreeMap<String,Command>();
	private PrintWriter out;
	private String name;
	
	private LoginRegistry loginRegistry;
	private String usernameActiveSession;
	private Long tokenActiveSession;

	public Shell(String n){ 
		this(n, new PrintWriter(System.out, true), true);
	}

	public Shell(String n, Writer w){ 
		this(n, w, true); 
	}

	public Shell(String n, Writer w, boolean flush){
		name = n;
		out = new PrintWriter(w, flush);
		this.loginRegistry = new LoginRegistry();
		this.loginGuestUser();		
		
		new Command(this, "quit", "quit the command interpreter"){
			void execute(String[] args){
				System.out.println(name+" quit");
				System.exit(0);
			}
		};

		new Command(this, "exec", "execute an external command"){
			void execute(String[] args){ 
				try{ 
					Sys.output(out); Sys.main(args);
				} 
				catch(Exception e){ 
					throw new RuntimeException(""+e);
				}
			}
		};

		new Command(this, "run", "run a class method"){
			void execute(String[] args){ 
				try{
					if(args.length > 0)
						shell().run(args[0], Arrays.copyOfRange(args, 1, args.length));
					else throw new Exception("Nothing to run!");
				} 
				catch(Exception e){ 
					throw new RuntimeException(""+e);
				}
			}
		};

		new Command(this, "help", "this command help"){
			void execute(String[] args){ 
				if(args.length == 0){
					for(String s: shell().list()) System.out.println(s);
					System.out.println(name()+" name (for command details)");
				} 
				else{
					for(String s: args)
						if(shell().get(s) != null)
							System.out.println(shell().get(s).help()); 
				}
			}
		};
	}
	
	private void loginGuestUser() {
		MyDrive mydrive = MyDrive.getInstance();
		LoginManager loginManager = mydrive.getLoginManager();
		Long guestToken = loginManager.createSession(Nobody.USERNAME, null);
		this.setUsernameActiveSession(Nobody.USERNAME);
		this.setTokenActiveSession(guestToken);
		this.loginRegistry.addNewToken(Nobody.USERNAME, guestToken);
	}
	
	public String getUsernameActiveSession() {
		return this.usernameActiveSession;
	}
	
	public Long getTokenActiveSession() {
		return this.tokenActiveSession;
	}
	
	public void setUsernameActiveSession(String usernameActiveSession) {
		this.usernameActiveSession = usernameActiveSession;
	}

	public void setTokenActiveSession(Long tokenActiveSession) {
		this.tokenActiveSession = tokenActiveSession;
	}

	public void addNewToken(String username, Long token) {
		this.removeGuestLogin(username);
		this.loginRegistry.addNewToken(username, token);
	}
	
	private void removeGuestLogin(String username) {
		if (username.equals(Nobody.USERNAME)) {
			return;
		}
		MyDrive myDrive = MyDrive.getInstance();
		LoginManager loginManager = myDrive.getLoginManager();
		loginManager.removeGuestSession();
	}
	
	public void changeActiveUserTo(String newActiveUsername) {
		if (!(this.loginRegistry.hasEntryForUsername(newActiveUsername))) {
			throw new InvalidUsernameException(newActiveUsername);
		}
		
		this.usernameActiveSession = newActiveUsername;
		this.tokenActiveSession = this.loginRegistry.getLastTokenByUsername(newActiveUsername);
		log.trace("Changed active user to " + newActiveUsername);
	}

	public void print(String s){ 
		out.print(s); 
	}

	public void println(String s){ 
		out.println(s); 
	}

	public void flush(){ 
		out.flush(); 
	}

	boolean add(Command c){
		return coms.put(c.name(), c) == null ? true : false;
	}

	public Command get(String s){
		return coms.get(s);
	}

	public Collection<String> list(){
		return Collections.unmodifiableCollection(coms.keySet());
	}

	public void execute() throws Exception{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String str, prompt = null; 

		if (prompt == null) prompt = "$ ";
		System.out.println(name+" shell ('quit' to leave)");
		System.out.print(prompt);
		while((str = in.readLine()) != null){
			String[] arg = str.split(" ");
			Command c = coms.get(arg[0]);
			if(c != null){
				try{
					c.execute(Arrays.copyOfRange(arg, 1, arg.length));
				} 
				catch (RuntimeException e){
					System.err.println(arg[0]+": "+e);
					e.printStackTrace();
				}
			} 
			else{
				if (arg[0].length() > 0){ 
					System.err.println(arg[0]+": command not found. ('help' for command list)");
				}
			}
			System.out.print(prompt);
		}
		System.out.println(name+" end");
	}
	
	public static void run(String name, String[] args) throws ClassNotFoundException, SecurityException, 
	NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

		Class<?> cls;
		Method meth;
		try{ 
			cls = Class.forName(name);
			meth = cls.getMethod("main", String[].class);
		} 
		catch(ClassNotFoundException cnfe){ 
			int pos;
			if ((pos = name.lastIndexOf('.')) < 0){
				throw cnfe;
			}
			cls = Class.forName(name.substring(0, pos));
			meth = cls.getMethod(name.substring(pos+1), String[].class);
		}
		meth.invoke(null, (Object)args); 
	}

	private static boolean wildcard(String text, String pattern){
		String[] cards = pattern.split("\\*");

		for(String card : cards){
			int idx = text.indexOf(card);
			if(idx == -1) return false; 
			text = text.substring(idx + card.length());
		}
		return true;
	}
	
	protected class LoginRegistry {
		private HashMap<String, Long> tokensOfLoggedSessions;
		
		public LoginRegistry() {
			this.tokensOfLoggedSessions = new HashMap<String, Long>();
		}
		
		public void addNewToken(String username, Long token) {
			if (tokensOfLoggedSessions.containsKey(Nobody.USERNAME)) {
				this.removeGuestSession();
			}
			tokensOfLoggedSessions.put(username, token);
		}
		
		private void removeGuestSession() {
			this.tokensOfLoggedSessions.remove(Nobody.USERNAME);
		}
		
		public Long getLastTokenByUsername(String username) {
			Optional<Long> maybeValue;
			maybeValue = Optional.ofNullable(this.tokensOfLoggedSessions.get(username));
			Long value = maybeValue.orElseThrow(() -> 
			new InvalidUsernameException("Couldn't find any tokens for username <" + username + ">"));
			
			return value;
		}
		
		public boolean hasEntryForUsername(String username) {
			return this.tokensOfLoggedSessions.containsKey(username);
		}
	}
}
