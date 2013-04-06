package edu.ucsb.cs56.S12.m_a_p.cp3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.*;

/** RecipePanel is panel that holds all the components for the recipe cookbook 


 */


public class RecipePanel extends JPanel implements ActionListener, ListSelectionListener{

	RecipeList list = loadList();

	JList listNames;
	DefaultListModel listModel;

	JLabel recipeInfo;
	int index = 0;
	JPanel RecipesListed;   
	JPanel recipeBox;

	JFileChooser fc;

	/**
    no-arg constructor constructs the JPanel and adds all the components
	 */

	public RecipePanel(){
		super(new BorderLayout());  

		//title image
		JLabel titleLabel = new JLabel(new ImageIcon(this.getClass().getResource("images/title.jpg"), "title"),JLabel.CENTER);

		//Label that actually contains the recipe info
		recipeInfo = new JLabel(printInfo());

		// set size and background of recipe info label
		Dimension preferredSize  = new Dimension(300,list.get(index).getList().size()*35);
		recipeInfo.setPreferredSize(preferredSize);
		recipeInfo.setBackground(Color.WHITE);
		recipeInfo.setOpaque(true);

		//make new scroll pane to hold recipe info
		JScrollPane RecipeInfoScroller  = new JScrollPane(recipeInfo);
		Border titled = new TitledBorder("Info:");
		RecipeInfoScroller .setBorder(titled); 
		RecipeInfoScroller .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		RecipeInfoScroller .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);  
		RecipeInfoScroller .setBackground(Color.WHITE);

		//set up a list of recipes
		listModel = new DefaultListModel();
		String[] listMembers = makeRecipeList();
		for(String s : listMembers)
			listModel.addElement(s);

		//use list of recipes to make a JList and set up how it works
		listNames = new JList(listModel);
		JScrollPane scroller = new JScrollPane(listNames);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);  
		listNames.setVisibleRowCount(10);
		listNames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listNames.addListSelectionListener(this);
		listNames.setSelectedIndex(0);

		//make a jpanel for the new JList and give it a border
		RecipesListed = new JPanel(new BorderLayout());
		RecipesListed.add(listNames, BorderLayout.CENTER);
		Border titled2 = new TitledBorder("Recipe's");
		RecipesListed.setBorder(titled2); 
		RecipesListed.setBackground(Color.WHITE);

		//make new jpanel that holds the title label and recipe info 
		JPanel contents = new JPanel(new BorderLayout());  
		contents.add(titleLabel , BorderLayout.NORTH);
		contents.add(RecipeInfoScroller  , BorderLayout.CENTER);
		contents.setBackground(Color.WHITE);

		//makes a menu at top of frame
		JMenuBar menuBar = new JMenuBar();
		JMenu m = new JMenu("File");

		//makes menu items 
		JMenuItem newMenuItem = new JMenuItem("Add New Recipe");
		JMenuItem newMenuItemDel = new JMenuItem("Delete Selected Recipe");
		JMenuItem newMenuItemLoadList = new JMenuItem("Load a recipe list");
		JMenuItem newMenuItemSaveList = new JMenuItem("Save recipe list");

		//add action listeners for menu items
		newMenuItem.addActionListener(this);
		newMenuItemDel.addActionListener(new deleteRecipe());
		newMenuItemLoadList.addActionListener(new fileLoader());
		newMenuItemSaveList.addActionListener(new fileSaver());

		//add menu items to menu and add menu to menubar
		m.add(newMenuItem);
		m.add(newMenuItemDel);
		m.add(newMenuItemLoadList);
		m.add(newMenuItemSaveList);

		menuBar.add(m);

		//add everything to this JPanel
		add(RecipesListed, BorderLayout.LINE_START);
		add(menuBar , BorderLayout.PAGE_START);
		add(contents , BorderLayout.CENTER);

	}//end RecipePanel() no arg constructor

	/**
    action performed method that listens for the user to puch the button to add a recipe to the current list
	 */

	public void actionPerformed(ActionEvent event) {
		RecipeAdder adder = new RecipeAdder(list, listModel, listNames);

	}//end actionPerformed method

	/**
    valueChanged listens for the user to select something on the JList 
	 */


	public void valueChanged(ListSelectionEvent lse){

		if(!lse.getValueIsAdjusting()){
			index = listNames.getSelectedIndex();  
			String info = printInfo() + " ";
			recipeInfo.setText(info);                 
			Dimension preferredSize  = new Dimension(300,info.lastIndexOf(" ")/2);
			recipeInfo.setPreferredSize(preferredSize);
		}


	}   


	/**
    printInfo gets the selected recipe and returns a string with that information 
	 */

	public String printInfo(){
		if(index > (list.size() - 1) || index < 0)
			return "error";
		return list.get(index).printRecipe();      
	}



	/**
    makes a list of the recipe names in a string array
	 */

	public String[] makeRecipeList(){
		String[] listEntries;
		listEntries = new String[list.size()];

		for(int i = 0; i<list.size();i++)
			listEntries[i] = list.get(i).getName();

		return listEntries;
	}


	/**
    an inner class the loades a recipeList from a file when the program starts
	 */
	public RecipeList loadList(){

		RecipeList recipes = new RecipeList(new Recipe("My First recipe"));

		try {
			URL url = new URL("http://www.cs.ucsb.edu/~m_a_p/cs56/S12/issues/0000754/browse/list.ser"); 
			ObjectInputStream is = new ObjectInputStream(url.openStream());
			recipes = (RecipeList) is.readObject();
			return recipes;
		} catch(Exception ex){
			ex.printStackTrace();
		}


		return recipes;
	}

	/**
    an inner class that deletes a recipe based on when the user presses the appropiate button 
	 */
	public class deleteRecipe implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			index = listNames.getSelectedIndex();  
			list.remove(index);	
			listModel.remove(index);

			listNames.setModel(listModel);
			listNames.setSelectedIndex(0);
			//saveList(list);
		}

	}


	/**
    an inner class that opens a recipeList based when the user presses the appropiate button 
	 */
	public class fileLoader implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(listNames);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				try {
					ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
					list = (RecipeList) is.readObject();
				} catch(Exception ex){
					ex.printStackTrace(); 

				} finally{
					listModel = listModel = new DefaultListModel();
					String[] listMembers = makeRecipeList();
					for(String s : listMembers)
						listModel.addElement(s);
					listNames.setModel(listModel);
					listNames.setSelectedIndex(0);
				}


			}


		}

	}//end file loader

	/**
    an inner class that saves a recipeList based when the user presses the appropiate button 
	 */
	public class fileSaver implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(listNames);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				try {
					FileOutputStream fs = new FileOutputStream(file);
					ObjectOutputStream os = new ObjectOutputStream(fs);

					os.writeObject(list);
					os.close();
				} catch(Exception ex) {
					ex.printStackTrace();
				}                  

			}
		}

	}//end of fileSaver



}