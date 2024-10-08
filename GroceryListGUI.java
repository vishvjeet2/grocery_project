import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class GroceryListGUI extends JFrame {
    private DefaultListModel<String> listModel;
    private JList<String> groceryList;
    private JTextField itemField;
    private JButton addButton;
    private JButton removeButton;
    private JButton clearButton;
    private JButton recipeButton;
    private JFileChooser fileChooser;
    private File groceryFile;

    public GroceryListGUI() {
        // Set up the GUI components
        setTitle("Grocery List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400, 300);

        listModel = new DefaultListModel<String>();
        groceryList = new JList<String>(listModel);
        JScrollPane scrollPane = new JScrollPane(groceryList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        itemField = new JTextField();
        inputPanel.add(itemField, BorderLayout.CENTER);
        addButton = new JButton("Add");
        addButton.addActionListener(new AddButtonListener());
        inputPanel.add(addButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        removeButton = new JButton("Remove");
        removeButton.addActionListener(new RemoveButtonListener());
        buttonPanel.add(removeButton);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ClearButtonListener());
        buttonPanel.add(clearButton);
        recipeButton = new JButton("Recipe");
        recipeButton.addActionListener(new RecipeButtonListener());
        buttonPanel.add(recipeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Set up the file chooser
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Grocery List");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Load the grocery list from a file, if available
        try {
            groceryFile = new File("groceryList.txt");
            BufferedReader reader = new BufferedReader(new FileReader(groceryFile));
            String line;
            int counter=0;
            while ((line = reader.readLine()) != null) {
                listModel.addElement(line);
                counter++;
            }
            if (counter==0){
                JOptionPane.showMessageDialog(GroceryListGUI.this, "Nothing in grocery list till now");
            }
            reader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(GroceryListGUI.this, "Nothing in grocery list till now");
        }
    }

private class AddButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        String item = itemField.getText();
        if (!item.isEmpty()) {
            listModel.addElement(item);
            itemField.setText("");
        }
    }
}

private class RemoveButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        int selectedIndex = groceryList.getSelectedIndex();
        if (selectedIndex != -1) {
            listModel.remove(selectedIndex);
        }
    }
}

private class ClearButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        listModel.clear();
    }
}

private class RecipeButtonListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        String[] items = new String[listModel.getSize()];
        for (int i = 0; i < listModel.getSize(); i++) {
            items[i] = listModel.getElementAt(i).toLowerCase();
        }
        
        RecipeSuggester suggester = new RecipeSuggester(items);
        String recipe = suggester.getSuggestedRecipe();
        if (recipe != null) {
            JOptionPane.showMessageDialog(GroceryListGUI.this, "Suggested recipe: " + recipe);
        } else {
            JOptionPane.showMessageDialog(GroceryListGUI.this, "No recipe suggestions available.");
        }
    }
}

public void saveListToFile() {
    try {
        BufferedWriter writer = new BufferedWriter(new FileWriter(groceryFile));
        for (int i = 0; i < listModel.getSize(); i++) {
            writer.write(listModel.getElementAt(i));
            writer.newLine();
        }
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static void main(String[] args) {
    GroceryListGUI gui = new GroceryListGUI();
    gui.setVisible(true);
    gui.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            gui.saveListToFile();
        }
    });
}
}