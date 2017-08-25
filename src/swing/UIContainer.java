package swing;

import utils.Language;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Author : zhouyx
 * Date   : 2017/8/20
 * Description :
 */
public class UIContainer {

    private UIContainer uiContainer;

    private JFrame frame;
    private JPanel rootPanel;
    private JTextArea appKeyArea;
    private JTextArea appSecurityArea;
    private JLabel locationPathLabel;
    private JButton changePathButton;
    private JTextArea progressDetailTextArea;
    private JPanel languagePanel;
    private ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    private JRadioButton androidRadioButton;
    private JRadioButton iOSRadioButton;
    private JButton startTranslateButton;

    private String filePath = "";
    private boolean isWindowsStyle;

    private OnButtonClickListener onButtonClickListener;

    public UIContainer() {
        this.uiContainer = this;
        try {
            // 将LookAndFeel设置成Windows样式
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            isWindowsStyle = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            UIManager.put("Button.select", new ColorUIResource(new Color(80, 85, 85)));
            isWindowsStyle = false;
        }
        frame = createFrame();
        rootPanel = createPanel(new FlowLayout());
        appKeyArea = createEditableTextArea();
        appKeyArea.setPreferredSize(new Dimension(300, 25));
        appKeyArea.setText("在这里输入app key");
        appSecurityArea = createEditableTextArea();
        appSecurityArea.setPreferredSize(new Dimension(300, 25));
        appSecurityArea.setText("在这里输入app security");
        locationPathLabel = createLabel("选择需要翻译的文件");
        locationPathLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
        locationPathLabel.setPreferredSize(new Dimension(550, 25));
        changePathButton = createButton("更改文件");
        progressDetailTextArea = createTextArea();
        languagePanel = createPanel(new FlowLayout((FlowLayout.LEFT)));
        for (Language language : Language.values()) {
            JCheckBox checkBox = createCheckBox(language.getName());
            languagePanel.add(checkBox);
            checkBoxes.add(checkBox);
        }

        androidRadioButton = createRadioButton("Android");
        androidRadioButton.setSelected(true);
        iOSRadioButton = createRadioButton("iOS");
        ButtonGroup group = new ButtonGroup();
        group.add(androidRadioButton);
        group.add(iOSRadioButton);
        startTranslateButton = createButton("开始转换");

        draw();
        setListener();
        frame.setContentPane(rootPanel);
        frame.setVisible(true);
    }

    private void draw() {
        Box appKeyBox = Box.createHorizontalBox();
        JPanel keyPanel = createPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(createLabel("百度APP KEY : "));
        keyPanel.add(appKeyArea);
        appKeyBox.add(keyPanel);

        Box appSecurityBox = Box.createHorizontalBox();
        JPanel securityPanel = createPanel(new FlowLayout(FlowLayout.LEFT));
        securityPanel.add(createLabel("百度SECURITY KEY : "));
        securityPanel.add(appSecurityArea);
        appSecurityBox.add(securityPanel);

        Box locationBox = Box.createHorizontalBox();
        JPanel locationPanel = createPanel(new BorderLayout());
        locationPanel.add(createLabel("位置："), "West");
        locationPanel.add(locationPathLabel, "Center");
        locationPanel.add(changePathButton, "East");
        locationBox.add(locationPanel);

        Box checkBox = Box.createHorizontalBox();
        checkBox.add(androidRadioButton);
        checkBox.add(Box.createHorizontalStrut(20));
        checkBox.add(iOSRadioButton);
        JPanel bottomPanel = createPanel(new BorderLayout());
        bottomPanel.add(checkBox, "West");
        bottomPanel.add(startTranslateButton, "East");

        Box baseBox = Box.createVerticalBox();
        baseBox.add(Box.createVerticalStrut(25));
        baseBox.add(appKeyBox);
        baseBox.add(Box.createVerticalStrut(5));
        baseBox.add(appSecurityBox);
        baseBox.add(Box.createVerticalStrut(15));
        baseBox.add(locationBox);
        baseBox.add(Box.createVerticalStrut(15));
        baseBox.add(createScrollPane(progressDetailTextArea));
        baseBox.add(Box.createVerticalStrut(30));
        baseBox.add(languagePanel);
        baseBox.add(Box.createVerticalStrut(30));
        baseBox.add(bottomPanel);
        rootPanel.add(baseBox);
    }

    private void setListener() {
        changePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = createFileChooser();
                File selectFile = fileChooser.getSelectedFile();
                if (selectFile == null) {
                    return;
                }
                filePath = selectFile.getAbsolutePath();
                locationPathLabel.setText(filePath);
            }
        });
        startTranslateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onButtonClickListener != null) {
                    ArrayList<Language> languages = new ArrayList<>();
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        JCheckBox checkBox = checkBoxes.get(i);
                        if (checkBox.isSelected()) {
                            languages.add(Language.values()[i]);
                        }
                    }
                    onButtonClickListener.onStartTranslateClick(uiContainer, filePath, androidRadioButton.isSelected(),
                            languages, appKeyArea.getText(), appSecurityArea.getText());
                }
            }
        });
        progressDetailTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                progressDetailTextArea.setCaretPosition(progressDetailTextArea.getText().length());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    public void setMessage(String text) {
        progressDetailTextArea.setText(text);
        progressDetailTextArea.paintImmediately(progressDetailTextArea.getBounds());
    }

    public void resetMessage() {
        progressDetailTextArea.setText("");
        progressDetailTextArea.paintImmediately(progressDetailTextArea.getBounds());
    }

    public void appendMessage(String text) {
        progressDetailTextArea.append(text + "\n");
        progressDetailTextArea.paintImmediately(progressDetailTextArea.getBounds());
        progressDetailTextArea.selectAll();
    }

    public void setStartTranslateButtonEnable(boolean enable) {
        if (enable) {
            startTranslateButton.setText("开始转换");
            startTranslateButton.setEnabled(true);
        } else {
            startTranslateButton.setText("正在转换");
            startTranslateButton.setEnabled(false);
        }
    }

    private JCheckBox createCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFocusPainted(false);
        return checkBox;
    }

    private JRadioButton createRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setFocusPainted(false);
        return radioButton;
    }

    private JFileChooser createFileChooser() {
        JFileChooser fileChooser = new JFileChooser(filePath);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showDialog(frame, "选择");
        return fileChooser;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setBackground(new Color(255, 255, 255, 0));
        label.setForeground(new Color(255, 255, 255));
        return label;
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBackground(new Color(255, 255, 255, 0));
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        return textArea;
    }

    private JTextArea createEditableTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setBackground(new Color(255, 255, 255, 0));
        textArea.setForeground(new Color(255, 255, 255));
        textArea.setOpaque(false);
        return textArea;
    }

    private JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBackground(new Color(255, 255, 255, 0));
        scrollPane.setForeground(new Color(255, 255, 255, 0));
        scrollPane.setOpaque(false);
        scrollPane.setAutoscrolls(false);
        scrollPane.setPreferredSize(new Dimension(800, 280));
        scrollPane.setMinimumSize(new Dimension(800, 280));
        scrollPane.setMaximumSize(new Dimension(800, 280));
        scrollPane.setSize(new Dimension(800, 280));
        return scrollPane;
    }

    private JPanel createPanel(LayoutManager layoutManager) {
        JPanel panel = new JPanel(layoutManager);
        panel.setBackground(new Color(60, 63, 65));
        return panel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        if (!isWindowsStyle) {
            button.setBackground(new Color(80, 85, 85));
            button.setForeground(new Color(255, 255, 255));
        }
        button.setSize(new Dimension(90, 33));
        button.setFocusPainted(false);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);
        return button;
    }

    private JFrame createFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Translator");
        frame.setSize(new Dimension(900, 650));
        frame.setLocationRelativeTo(null);
        try {
            URL url = getClass().getResource("/res/logo.png");
            BufferedImage image = ImageIO.read(url);
            frame.setIconImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return frame;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        void onStartTranslateClick(UIContainer uiContainer, String filePath, boolean isAndroid,
                                   ArrayList<Language> languages, String appKey, String appSecurity);
    }

}
