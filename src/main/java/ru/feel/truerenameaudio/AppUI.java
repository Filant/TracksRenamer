/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.feel.truerenameaudio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import ru.feel.ui.CustomListElement;
import ru.feel.ui.CustomListModel;
import ru.feel.ui.CustomListRenderer;

/**
 *
 * @author Anton
 */
public class AppUI extends JFrame{
    private JPanel rootPanel = new JPanel();    
    private JList filesList = new JList();
    private JList selectedFilesList = new JList();
    private JScrollPane forSelectedFilesList = new JScrollPane(selectedFilesList);
    private JButton addButton = new JButton("Добавить");
    private JButton chooseButton = new JButton("Выбрать файлы");
    private JButton cancelButton = new JButton("Отмена");
    private JButton renameButton = new JButton("Переименовать");
    private JButton backButton = new JButton("Назад");
    private JButton deleteButton = new JButton("Удалить");
    private JDesktopPane searcheFiles = new JDesktopPane();
    private ImageIcon folderIcon = new ImageIcon("src/folder.png");
    private ImageIcon fileIcon = new ImageIcon("src/file.png");
    private JProgressBar progressBar = new JProgressBar();
    
    public AppUI (){
        super("TrueRenamer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        rootPanel.setLayout(new GridBagLayout());
        getContentPane().add(rootPanel);
        forSelectedFilesList.setPreferredSize(new Dimension(400, 500));
        forSelectedFilesList.setMinimumSize(new Dimension(300, 400));
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setMaximum(100);

        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JDialog searcheFilesDialog = new JDialog(AppUI.this, "Выбор треков", true);
                JPanel rootDialogPanel = new JPanel();
                searcheFilesDialog.add(rootDialogPanel);
                File discs[] = File.listRoots();               
                final ArrayList <String> cashRootFiles = new ArrayList <String>();
                JScrollPane filesScrol = new JScrollPane(filesList);
                filesList.setCellRenderer(new CustomListRenderer());
                filesScrol.setPreferredSize(new Dimension(400, 500));                
                filesList.setListData(discs);
                filesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                
                filesList.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getClickCount() == 2){
                            CustomListModel model = new CustomListModel();
                            String pathSelected = filesList.getSelectedValue().toString();                           
                            String summaPath = ToWholePath(cashRootFiles);
                            
                            if(cashRootFiles.size() > 1){
                                summaPath = summaPath + "\\" + pathSelected;
                            }else{
                                summaPath = summaPath + pathSelected;
                            }
                            File file = new File(summaPath); 
                            
                            if(file.isDirectory()){
                                String[] rootStr = file.list();    
                                for (String str : rootStr) {
                                    File checkForHidden = new File (file.getPath()+ "\\" + str);
                                    if(!checkForHidden.isHidden()){
                                        if(checkForHidden.isDirectory()){
                                            model.addElement(new CustomListElement(str, folderIcon));
                                        }else{
                                            model.addElement(new CustomListElement(str, fileIcon));
                                        }
                                    }               
                                }
                                if(cashRootFiles.size() <= 1){
                                    cashRootFiles.add(pathSelected);
                                }else{
                                    cashRootFiles.add("\\" + pathSelected);
                                }
                                filesList.setModel(model);    
                            }                              
                        }
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                
                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(cashRootFiles.size() > 1){
                            cashRootFiles.remove(cashRootFiles.size() - 1);
                            String backDir = ToWholePath(cashRootFiles);
                            String[] rootStr = new File(backDir).list();
                            CustomListModel backRootModel = new CustomListModel();

                            for (String str : rootStr) {
                                File checkForHidden = new File (backDir + "\\" + str);
                                if(!checkForHidden.isHidden()){
                                    if(checkForHidden.isDirectory()){
                                        backRootModel.addElement(new CustomListElement(str, folderIcon));
                                    }else{
                                        backRootModel.addElement(new CustomListElement(str, fileIcon));
                                    }
                                }               
                            }
                            filesList.setModel(backRootModel);                                                              
                        }else{
                            File discs[] = File.listRoots();
                            cashRootFiles.removeAll(cashRootFiles);
                            filesList.setListData(discs);
                        }
                    }
                });
                
                addButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                        ListModel model = filesList.getModel();
                        int [] selectedIndices = filesList.getSelectedIndices();
                        List <String> selectedFiles = new ArrayList<String>();
                        String filePath = ToWholePath(cashRootFiles);
                        
                        for(int i : selectedIndices){
                            String selectedFileName = model.getElementAt(i).toString();
                            selectedFiles.add(filePath + "\\" + selectedFileName);
                        }
                                               
                        selectedFilesList.setListData(selectedFiles.toArray());                          
                    }
                });
                
                rootDialogPanel.setLayout(new GridBagLayout());
                rootDialogPanel.add(backButton, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.WEST, GridBagConstraints.CENTER, new Insets(10, 10, 6, 10), 0, 0));
                rootDialogPanel.add(filesScrol, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(6, 6, 6, 6), 0, 0));
                rootDialogPanel.add(addButton, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(10, 10, 6, 10), 0, 0));
                rootDialogPanel.add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(10, 10, 6, 10), 0, 0));
                           
                searcheFilesDialog.pack();
                searcheFilesDialog.setLocationRelativeTo(null);
                searcheFilesDialog.setVisible(true);
            }
        });
        
        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(selectedFilesList.getComponent(0) == null) return;
                ListModel model = selectedFilesList.getModel();
                List<File> filesToRenameList = new ArrayList<>();
                for(int i = 0; i < model.getSize(); i++)
                    filesToRenameList.add(new File(model.getElementAt(i).toString()));
                             
                progressBar.setStringPainted(true);
                progressBar.setMinimum(0);
                progressBar.setValue(0);
                progressBar.setMaximum(100);              
                InAudio inA = new InAudio(filesToRenameList);            
                inA.setJProgress(progressBar);
                inA.start();                                
            }
        });
        
        rootPanel.add(forSelectedFilesList, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(12, 12, 6, 12), 0, 0));
        rootPanel.add(chooseButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(6, 6, 6, 6), 0, 0));
        rootPanel.add(deleteButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(6, 6, 6, 6), 0, 0));
        rootPanel.add(renameButton, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(6, 6, 6, 6), 0, 0)); 
        rootPanel.add(progressBar, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.CENTER, new Insets(6, 6, 6, 6), 0, 0));
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
    }
    
    public String ToWholePath (List<String> file){
        String listPart = "";
        for(String str : file)
            listPart = listPart + str;            
        return listPart;        
    }
}
