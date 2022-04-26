package editor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    public TextEditor() {
        setTitle("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        init();
        setVisible(true);
    }


    int listIndex = 0;
    List<Integer> list = new ArrayList<>();
    int wordLength = 0;

    protected void init() {
        Font font = new Font("Courier", Font.PLAIN, 15);

        //creating panels

        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        setMargin(upperPanel, 10, 10, 0, 15);

        JPanel lowerPanel = new JPanel();

        JTextField textField = new JTextField();
        textField.setName("SearchField");
        textField.setPreferredSize(new Dimension(150, 30));

        JButton loadButton = new JButton();
        ImageIcon loadIcon = new ImageIcon("images\\load.png");
        loadIcon = changeSize(loadIcon, 30, 30);
        loadButton.setIcon(loadIcon);
        loadButton.setName("OpenButton");
        loadButton.setPreferredSize(new Dimension(30, 30));

        JButton saveButton = new JButton();
        ImageIcon saveIcon = new ImageIcon("images\\save.png");
        saveIcon = changeSize(saveIcon, 30, 30);
        saveButton.setIcon(saveIcon);
        saveButton.setName("SaveButton");
        saveButton.setPreferredSize(new Dimension(30, 30));

        JButton searchButton = new JButton();
        ImageIcon searchIcon = new ImageIcon("images\\search.png");
        searchIcon = changeSize(searchIcon, 30, 30);
        searchButton.setIcon(searchIcon);
        searchButton.setName("StartSearchButton");
        searchButton.setPreferredSize(new Dimension(30, 30));

        JButton nextButton = new JButton();
        ImageIcon nextIcon = new ImageIcon("images\\next.png");
        nextIcon = changeSize(nextIcon, 30, 30);
        nextButton.setIcon(nextIcon);
        nextButton.setName("NextMatchButton");
        nextButton.setPreferredSize(new Dimension(30, 30));

        JButton previousButton = new JButton();
        ImageIcon previousIcon = new ImageIcon("images\\previous.png");
        previousIcon = changeSize(previousIcon, 30, 30);
        previousButton.setIcon(previousIcon);
        previousButton.setName("PreviousMatchButton");
        previousButton.setPreferredSize(new Dimension(30, 30));

        JCheckBox checkBox = new JCheckBox(" Use regex");
        checkBox.setName("UseRegExCheckbox");

        JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fileChooser.setName("FileChooser");
        fileChooser.setVisible(false);

        JTextArea textArea = new JTextArea();
        textArea.setName("TextArea");

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        scrollPane.setPreferredSize(new Dimension(450, 220));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        searchButton.addActionListener(actionEvent -> {
            boolean checkBoxSelected = checkBox.isSelected();
            new SwingSearchWorker(textArea, textField, checkBoxSelected).execute();
        });


        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                findNext(textArea, textField);
            }
        });

        previousButton.addActionListener(actionEvent -> {
            findPrevious(textArea, textField);
        });


        loadButton.addActionListener(actionEvent -> {
            fileChooser.setVisible(true);
            int returnValue = fileChooser.showOpenDialog(null);
            String path = null;
            if (returnValue == fileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
            }
            String text = readFile(path);
            textArea.setText(null);
            textArea.setText(text);
        });

        saveButton.addActionListener(actionEvent -> {
            fileChooser.setVisible(true);
            int returnValue = fileChooser.showSaveDialog(null);
            String path = null;
            if (returnValue == fileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
            }

            String text = textArea.getText();
            writeFile(text, path);
        });

        upperPanel.add(loadButton);
        upperPanel.add(saveButton);
        upperPanel.add(textField);
        upperPanel.add(searchButton);
        upperPanel.add(previousButton);
        upperPanel.add(nextButton);
        upperPanel.add(checkBox);
        upperPanel.add(fileChooser);

        lowerPanel.add(scrollPane);

        add(upperPanel, BorderLayout.NORTH);
        add(lowerPanel, BorderLayout.CENTER);

        //creating menu

        JMenuBar jMenuBar = new JMenuBar();
        setJMenuBar(jMenuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setMnemonic(KeyEvent.VK_S);

        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.setName("MenuOpen");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");

        JMenuItem startSearchMenuItem = new JMenuItem("Start search");
        startSearchMenuItem.setName("MenuStartSearch");
        JMenuItem previousSearchMenuItem = new JMenuItem("Previous search");
        previousSearchMenuItem.setName("MenuPreviousMatch");
        JMenuItem nextMatchMenuItem = new JMenuItem("Next match");
        nextMatchMenuItem.setName("MenuNextMatch");
        JMenuItem useRegexpMenuItem = new JMenuItem("Use regular expressions");
        useRegexpMenuItem.setName("MenuUseRegExp");

        loadMenuItem.addActionListener(actionEvent -> {
            fileChooser.setVisible(true);
            int returnValue = fileChooser.showOpenDialog(null);
            String path = null;
            if (returnValue == fileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
            }
            String text = readFile(path);
            textArea.setText(null);
            textArea.setText(text);
        });

        saveMenuItem.addActionListener(actionEvent -> {
            fileChooser.setVisible(true);
            int returnValue = fileChooser.showSaveDialog(null);
            String path = null;
            if (returnValue == fileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
            }

            String text = textArea.getText();
            writeFile(text, path);
        });

        startSearchMenuItem.addActionListener(actionEvent -> {
            if (checkBox.isSelected()) {
                searchRegexp(textArea, textField);
            } else {
                searchCertain(textArea, textField);
            }
        });

        previousSearchMenuItem.addActionListener(actionEvent -> findPrevious(textArea, textField));

        nextMatchMenuItem.addActionListener(actionEvent -> findNext(textArea, textField));

        useRegexpMenuItem.addActionListener(actionEvent -> {
            checkBox.doClick();
        });

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        searchMenu.add(startSearchMenuItem);
        searchMenu.add(previousSearchMenuItem);
        searchMenu.add(nextMatchMenuItem);
        searchMenu.add(useRegexpMenuItem);

        jMenuBar.add(fileMenu);
        jMenuBar.add(searchMenu);

    }


    protected List<Integer>  searchRegexp(JTextArea textArea, JTextField textField) {
        if (!list.isEmpty()) {
            list.clear();
            listIndex = 0;
            wordLength = 0;
        }
        String text = textArea.getText();
        String wordToFind = textField.getText();

        Pattern pattern = Pattern.compile(wordToFind);
        Matcher matcher = pattern.matcher(text);

        boolean first = true;
        while (matcher.find()) {
            list.add(matcher.start());
            if (first) {
                String foundedWord = text.substring(matcher.start(), matcher.end());
                wordLength = foundedWord.length();
                first = false;
            }
        }
        if (!list.isEmpty()) {
            textArea.setCaretPosition(list.get(0) + wordLength);
            textArea.select(list.get(0), list.get(0) + wordLength);
            textArea.grabFocus();
        }
        return list;
    }

    public List<Integer> searchCertain(JTextArea textArea, JTextField textField) {
        if (!list.isEmpty()) {
            list.clear();
            listIndex = 0;
            wordLength = 0;
        }
        String text = textArea.getText();
        String wordToFind = textField.getText();
        wordLength = wordToFind.length();

        if (text.contains(wordToFind)) {
            for (int i = -1; (i = text.indexOf(wordToFind, i + 1)) != -1; i++) {
                list.add(i);
            }
            textArea.setCaretPosition(list.get(0) + wordLength);
            textArea.select(list.get(0), list.get(0) + wordLength);
            textArea.grabFocus();
        }
        return list;
    }


    private void findNext(JTextArea textArea, JTextField textField) {
        listIndex++;
        if (listIndex == list.size()) {
            listIndex = 0;
        }

        int index = list.get(listIndex);

        textArea.setCaretPosition(index + wordLength);
        textArea.select(index, index + wordLength);
        textArea.grabFocus();

    }

    private void findPrevious(JTextArea textArea, JTextField textField) {

        listIndex--;
        if (listIndex < 0) {
            listIndex = list.size() - 1;
        }

        int index = list.get(listIndex);

        textArea.setCaretPosition(index + wordLength);
        textArea.select(index, index + wordLength);
        textArea.grabFocus();

    }


    private ImageIcon changeSize(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image newImg = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon(newImg);
    }

    public static void forceSize(JComponent component, int width, int height) {
        Dimension d = new Dimension(width, height);
        component.setMinimumSize(d);/*from  ww w .j ava  2 s .  c  o  m*/
        component.setMaximumSize(d);
        component.setPreferredSize(d);
    }

    private static void setMargin(JComponent aComponent, int aTop,
                                  int aRight, int aBottom, int aLeft) {

        Border border = aComponent.getBorder();

        Border marginBorder = new EmptyBorder(new Insets(aTop, aLeft,
                aBottom, aRight));//from   w ww. j  a va2s.  c  o m
        aComponent.setBorder(border == null ? marginBorder
                : new CompoundBorder(marginBorder, border));
    }

    private void writeFile(String text, String path) {
        try {
            Files.write(Paths.get(path), text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readFile(String path) {
        String result = "";
        try {
            result = Files.readString(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    class SwingSearchWorker extends SwingWorker<List<Integer>, Object> {
        boolean checkBoxSelected;
        JTextArea textArea;
        JTextField textField;

        public SwingSearchWorker(JTextArea textArea, JTextField textField, boolean checkBoxSelected) {
            this.textArea = textArea;
            this.textField = textField;
            this.checkBoxSelected = checkBoxSelected;
        }

        @Override
        protected List<Integer> doInBackground() throws Exception {
            if (checkBoxSelected) {
                return searchRegexp(textArea, textField);
            } else {
                return searchCertain(textArea, textField);
            }
        }
    }
}

