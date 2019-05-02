package org.mathias.CarnetAdresse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.gnu.gdk.Pixbuf;
import org.gnu.glade.GladeXMLException;
import org.gnu.glade.LibGlade;
import org.gnu.glib.JGException;
import org.gnu.gnome.Program;
import org.gnu.gtk.AboutDialog;
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnObject;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreePath;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.event.TreeSelectionEvent;
import org.gnu.gtk.event.TreeSelectionListener;

public class Application implements TreeSelectionListener
{
	private LibGlade glade;

	private Entry entryLastname;

	private Entry entryFirstname;
	
	private Entry entryPhone;

	private TreeView treeviewPersons;

	private ListStore listPersons;

	private AboutDialog aboutDialog;

	private DataColumnString columnFirstname;

	private DataColumnString columnLastname;
	
	private DataColumnObject columnPerson;

	private String filename = "data/carnetAdresse.data";

	/**
	 * construit l'application graphique à partir du fichier .glade et se
	 * branche à certain contrôles utiles
	 * charge une liste de personne depuis une fichier afin de remplir la liste
	 * 
	 * @throws GladeXMLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Application() throws GladeXMLException, FileNotFoundException,
			IOException
	{
		// chargement des contrôles glade
		glade = new LibGlade("data/data.glade", this);
		entryLastname = (Entry) glade.getWidget("entryLastname");
		entryFirstname = (Entry) glade.getWidget("entryFirstname");
		entryPhone = (Entry) glade.getWidget("entryPhone");
		treeviewPersons = (TreeView) glade.getWidget("treeviewPersons");
		
		
		treeviewPersons.getSelection()
				.addListener((TreeSelectionListener) this);

		// construction de la treeview
		columnFirstname = new DataColumnString();
		columnLastname = new DataColumnString();
		columnPerson = new DataColumnObject();
		listPersons = new ListStore(new DataColumn[] { columnFirstname,
				columnLastname, columnPerson });
		treeviewPersons.setModel(listPersons);

		CellRendererText renderText = new CellRendererText();
		TreeViewColumn colFirstname = new TreeViewColumn();
		colFirstname.packStart(renderText, true);
		colFirstname.setTitle("Prénom");
		colFirstname.addAttributeMapping(renderText,
				CellRendererText.Attribute.TEXT, columnFirstname);

		TreeViewColumn colLastname = new TreeViewColumn();
		colLastname.packStart(renderText, true);
		colLastname.setTitle("Nom");
		colLastname.addAttributeMapping(renderText,
				CellRendererText.Attribute.TEXT, columnLastname);		

		treeviewPersons.appendColumn(colFirstname);
		treeviewPersons.appendColumn(colLastname);

		// construction de la fiche About
		aboutDialog = new AboutDialog();
		aboutDialog.setAuthors(new String[] { "Mathias Kluba" });
		aboutDialog.setComments("Ceci est le plus beau produit du monde");
		try
		{
			Pixbuf logo = new Pixbuf("data/pixmaps/ILoveEggs.png");
			aboutDialog.setLogo(logo);
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JGException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		aboutDialog.setWebsite("http://grozeille.wordpress.com");

		// chargement des personnes à partir du fichier
		ArrayList list = null;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try
		{
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			list = (ArrayList) in.readObject();
			in.close();
			
			Iterator it = list.iterator();
			while(it.hasNext())
			{
				Person person = (Person)it.next();
				// ajoute la personne dans la liste
				addPerson(person);
			}
			
		} catch (IOException ex)
		{
			ex.printStackTrace();
		} catch (ClassNotFoundException ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * l'utilistateur a cliqué sur le bouton "ajouter", ajoute un contact à la
	 * liste
	 * 
	 */
	public void on_buttonAjouter_clicked()
	{
		treeviewPersons.getSelection().select(
				addPerson(new Person("nouveau", "nouveau")));	
		entryFirstname.grabFocus();
	}

	/**
	 * affiche la fiche About
	 *
	 */
	public void on_aboutMenu_activate()
	{
		aboutDialog.show();
	}

	/**
	 * l'utilisateur a cliqué sur le bouton "supprimer", supprime l'utilisateur
	 * sélectionné dans la liste
	 * 
	 */
	public void on_buttonSupprimer_clicked()
	{
		TreePath[] tp = treeviewPersons.getSelection().getSelectedRows();
		if (tp.length == 1)
		{
			TreeIter item = listPersons.getIter(tp[0].toString());		
			TreePath path = item.getPath();
			listPersons.removeRow(item);			
			treeviewPersons.getSelection().select(path);			
		}
	}

	/**
	 * l'utilisateur a cliqué sur le bouton "Quitter", donc quitte l'application
	 * 
	 */
	public void on_quitMenu_activate()
	{
		CloseApp();

	}

	/**
	 * sauvegarde les personnes dans un fichier puis quitte l'application
	 *
	 */
	private void CloseApp()
	{
		ArrayList list = new ArrayList();
		TreeIter item = listPersons.getFirstIter();
		while (item != null)
		{
			list.add(listPersons.getValue(item, columnPerson));
			item = item.getNextIter();
		}
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(list);
			out.close();
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		Gtk.mainQuit();
	}

	/**
	 * quitte l'application quand l'utilisateur ferme la fenêtre
	 * 
	 */
	public void on_windowMain_hide()
	{
		CloseApp();
	}

	/**
	 * point d'entré principal de l'application
	 * @param args
	 */
	public static void main(String[] args)
	{
		Program.initGnomeUI("CarnetAdresse", "1.0", new String[0]);
		Gtk.init(args);
		try
		{
			new Application();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		Gtk.main();
	}

	/**
	 * l'utilisateur a sélectionné une personnes de la liste, charge les infos
	 * de cette personne dans le panneau latéral
	 * 
	 */
	public void selectionChangedEvent(TreeSelectionEvent event)
	{
		TreePath[] tp = treeviewPersons.getSelection().getSelectedRows();
		if (tp.length == 1)
		{
			TreeIter item = listPersons.getIter(tp[0].toString());

			Person person = (Person) listPersons.getValue(item, columnPerson);
			entryLastname.setText(person.getLastName());
			entryFirstname.setText(person.getFirstname());
			entryPhone.setText(person.getPhone());
		} else if (tp.length == 0)
		{
			entryLastname.setText("");
			entryFirstname.setText("");
			entryPhone.setText("");
		}
	}

	/**
	 * l'utilisateur a changé le prénom
	 *
	 */
	public void on_entryFirstname_changed()
	{
		TreePath[] tp = treeviewPersons.getSelection().getSelectedRows();
		if (tp.length == 1)
		{
			TreeIter item = listPersons.getIter(tp[0].toString());

			Person person = (Person) listPersons.getValue(item, columnPerson);
			person.setFirstname(entryFirstname.getText());
			listPersons.setValue(item, columnFirstname, person.getFirstname());

		}
	}

	/**
	 * l'utilisateur a changé le nom
	 *
	 */
	public void on_entryLastname_changed()
	{
		TreePath[] tp = treeviewPersons.getSelection().getSelectedRows();
		if (tp.length == 1)
		{
			TreeIter item = listPersons.getIter(tp[0].toString());

			Person person = (Person) listPersons.getValue(item, columnPerson);
			person.setLastName(entryLastname.getText());
			listPersons.setValue(item, columnLastname, person.getLastName());
		}
	}

	/**
	 * l'utilisateur a changé le numéro de téléhone
	 *
	 */
	public void on_entryPhone_changed()
	{
		TreePath[] tp = treeviewPersons.getSelection().getSelectedRows();
		if (tp.length == 1)
		{
			TreeIter item = listPersons.getIter(tp[0].toString());

			Person person = (Person) listPersons.getValue(item, columnPerson);
			person.setPhone(entryPhone.getText());
		}
	}
	
	/**
	 * ajoute une personne à la liste (treeview)
	 * @param person
	 */
	private TreeIter addPerson(Person person)
	{

		TreeIter item = listPersons.appendRow();
		listPersons.setValue(item, columnFirstname, person.getFirstname());
		listPersons.setValue(item, columnLastname, person.getLastName());
		listPersons.setValue(item, columnPerson, person);
		return item;			
	}
}
