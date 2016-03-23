package pt.tecnico.myDrive.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import pt.tecnico.myDrive.domain.File;

public abstract class SetOrderingHelper {
	public static ArrayList<File> sortFileSetById(Set<File> toSort){
		ArrayList<File> sorted = new ArrayList<File>(toSort);
		Collections.sort(sorted, (file1, file2) -> file1.getId().compareTo(file2.getId()) );
		return sorted;
	}
}
