package pt.tecnico.myDrive.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import pt.tecnico.myDrive.domain.MyDrive;

public class ImportXMLServiceTest extends AbstractServiceTest {
    private final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
    + "<myDrive>"
    + "  <user username=\"jtb\">"
    + "    <password>fernandes</password>"
    + "    <name>Joaquim Teófilo Braga</name>"
    + "    <home>/home/jtb</home>"
    + "    <mask>rwxdr-x-</mask>"
    + "  </user>"
    + "  <user username=\"mja\">"
    + "    <name>Manuel José de Arriaga</name>"
    + "    <password>Peyrelongue</password>"
    + "  </user>"
    + "  <plain id=\"3\">"
    + "    <path>/home/jtb</path>"
    + "    <name>profile</name>"
    + "    <owner>jtb</owner>"
    + "    <perm>rw-dr---</perm>"
    + "    <contents>Primeiro chefe de Estado do regime republicano (acumulando com a chefia do governo), numa capacidade provisória até à eleição do primeiro presidente da República.</contents>"
    + "  </plain>"
    + "  <dir id=\"4\">"
    + "    <path>/home/jtb</path>"
    + "    <name>documents</name>"
    + "    <owner>jtb</owner>"
    + "    <perm>rwxdr-x-</perm>"
    + "  </dir>"
    + "  <link id=\"5\">"
    + "    <path>/home/jtb</path>"
    + "    <name>doc</name>"
    + "    <owner>jtb</owner>"
    + "    <perm>r-xdr-x-</perm>"
    + "    <value>/home/jtb/documents</value>"
    + "  </link>"
    + "  <dir id=\"7\">"
    + "    <path>/home/jtb</path>"
    + "    <owner>jtb</owner>"
    + "    <name>bin</name>"
    + "    <perm>rwxd--x-</perm>"
    + "  </dir>"
    + "  <app id=\"9\">"
    + "    <path>/home/jtb/bin</path>"
    + "    <name>hello</name>"
    + "    <owner>jtb</owner>"
    + "    <perm>rwxd--x-</perm>"
    + "    <method>pt.tecnico.myDrive.app.Hello</method>"
    + "  </app>"
    + "</myDrive>";
    
    protected void populate() {
    	MyDrive drive = MyDrive.getInstance();
    }

    @Test
    public void success() throws Exception {
	Document doc = new SAXBuilder().build(new StringReader(xml));
        ImportXMLService service = new ImportXMLService(doc);
        service.execute();

        // check imported data
        MyDrive drive = MyDrive.getInstance();
        assertEquals(4, drive.getUserSet().size());
        assertTrue(drive.hasUser("jtb"));
        assertTrue("created mja", drive.hasUser("mja"));
        assertEquals(11, drive.getFileSet().size());
	// it must be right, but more checks can be made ...
    }
}
