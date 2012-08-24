package de.lathspell.dtx2gp.frontend;

import de.lathspell.dtx2gp.DtxToTuxguitar;
import de.lathspell.dtx2gp.dtx.DTXFile;
import de.lathspell.dtx2gp.dtx.DTXReader;
import de.lathspell.dtx2gp.tg.TGHelper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.herac.tuxguitar.song.models.TGSong;

import javax.jnlp.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Gui extends javax.swing.JApplet {

    /**
     * Visible in the Java console.
     */
    private static final Logger log = Logger.getLogger(Gui.class.toString());
    /**
     * Buffer for the converted file.
     */
    private byte[] converted;

    /**
     * Initializes the applet Gui
     */
    @Override
    public void init() {
        log.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(Gui.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
        }
        //</editor-fold>

        /* Create and display the applet */
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    initComponents();
                }
            });
        } catch (Exception e) {
            alert("Error initializing: " + e);
        }
    }

    private void alert(String msg) {
        log.warn(msg);
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButtonUpload = new javax.swing.JButton();
        jButtonDownload = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        jLabel1.setFont(new java.awt.Font("Comic Sans MS", 1, 14)); // NOI18N
        jLabel1.setText("dtx2gp " + Version.getVersion());

        jButtonUpload.setText("1. upload file");
        jButtonUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUploadActionPerformed(evt);
            }
        });

        jButtonDownload.setText("2. download converted file");
        jButtonDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDownloadActionPerformed(evt);
            }
        });

        jLabel2.setText("http://sourceforge.net/projects/dtx2gp/");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jButtonDownload)
                                                        .addComponent(jButtonUpload)
                                                        .addComponent(jLabel1))
                                                .addGap(0, 142, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel2)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(40, 40, 40)
                                .addComponent(jButtonUpload)
                                .addGap(33, 33, 33)
                                .addComponent(jButtonDownload)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addContainerGap())
        );
    }// </editor-fold>

    private void jButtonUploadActionPerformed(java.awt.event.ActionEvent evt) {
        log.debug("entering");
        converted = null;
        try {
            String extensions[] = {"dtx"};
            String input = null;
            // Webstart or not?
            try {
                FileOpenService fos = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");
                FileContents fc = fos.openFileDialog("/", extensions);
                if (fc == null) {
                    log.debug("user canceled the file dialog.");
                } else {
                    log.info("opening " + fc.getName());
                    input = IOUtils.toString(fc.getInputStream());
                }
            } catch (UnavailableServiceException e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    log.info("opening " + file.getName());
                    input = IOUtils.toString(new BufferedInputStream(new FileInputStream(file)));
                } else {
                    log.debug("user canceled the file dialog.");
                }
            }

            log.info("converting DTXMania to TuxGuitar");
            DTXFile dtxfile = new DTXReader().readText(input);
            TGSong tgsong = new DtxToTuxguitar().convertSong(dtxfile);

            log.info("converting TuxGuitar to GuitarPro5");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new TGHelper().writeGP5Song(bos, tgsong);

            converted = bos.toByteArray();


        } catch (IOException e) {
            alert("Exception: " + e);
        }
    }

    private void jButtonDownloadActionPerformed(java.awt.event.ActionEvent evt) {
        log.debug("entering");

        if (converted == null) {
            alert("First load something!");
            return;
        }

        try {
            // Webstart or not?
            try {
                ByteArrayInputStream is = new ByteArrayInputStream(converted);
                FileSaveService fos = (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");
                fos.saveFileDialog(null, null, is, ".txt");
            } catch (UnavailableServiceException e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    log.info("saving to " + file.getName());
                    IOUtils.write(converted, new BufferedOutputStream(new FileOutputStream(file)));
                } else {
                    log.debug("user canceled the file dialog");
                }
            }
        } catch (Exception e) {
            alert("Exception: " + e);
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton jButtonDownload;
    private javax.swing.JButton jButtonUpload;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration

    public static void main(String args[]) {
        Gui g = new Gui();
        g.init();
        g.start();

        JFrame frame = new JFrame("Web Start Test");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        Container cp = frame.getContentPane();
        cp.add(g, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}