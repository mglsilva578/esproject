package pt.tecnico.myDrive.domain;

import pt.ist.fenixframework.FenixFramework;

public class MyDrive extends MyDrive_Base {
    
    private MyDrive() {
        super();
        setRoot(FenixFramework.getDomainRoot());
    }
    
    public static MyDrive getInstance(){
    	MyDrive drive = FenixFramework.getDomainRoot().getMydrive();
    	if( drive != null ){
    		return drive;
    	}else{
    		return new MyDrive();
    	}
    }
}
