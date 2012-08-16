package edu.amaes.nudetech.ui;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.*;
import edu.amaes.nudetech.algo.nuditydetect.ColoredImageNudityDetector;
import edu.amaes.nudetech.algo.nuditydetect.ImageNudityDetector;
import edu.amaes.nudetech.algo.video.frameextract.VideoFrameExtractor;
import edu.amaes.nudetech.algo.video.frameextract.VideoFrameExtractorImpl;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Angelo Balaguer
 */
public class FileUploaderPanel extends VerticalLayout {
    
    private Label fileName = new Label();
    private Label textualProgress = new Label();
    private ProgressIndicator progressIndicator = new ProgressIndicator();
    private FileUploaderPanel.LineBreakCounter counter = new FileUploaderPanel.LineBreakCounter();
    private Upload upload = new Upload(null, counter);

    public FileUploaderPanel() {
        setSpacing(true);
        setMargin(true);

        Label uploadLabel = new Label();
        uploadLabel.setCaption("Upload Image or Video");
        uploadLabel.setIcon(new ThemeResource("img/upload_icon.gif"));
        addComponent(uploadLabel);

        // make analyzing start immediatedly when file is selected
        upload.setImmediate(true);
        upload.setButtonCaption("Upload File");
        addComponent(upload);

        final Button cancelProcessing = new Button("Cancel Upload");
        cancelProcessing.addListener(new Button.ClickListener() {

            public void buttonClick(Button.ClickEvent event) {
                upload.interruptUpload();
            }
        });
        cancelProcessing.setVisible(false);
        cancelProcessing.setStyleName("small");

        Panel uploadDetailsPanel = new Panel("Upload Details");
        uploadDetailsPanel.setSizeUndefined();
        
        FormLayout uploadDetailsPanelLayout = new FormLayout();
        uploadDetailsPanelLayout.setMargin(true);
        uploadDetailsPanel.setContent(uploadDetailsPanelLayout);
        
        HorizontalLayout progressLayout = new HorizontalLayout();
        progressLayout.setSpacing(true);
        progressLayout.setCaption("Upload Progress");
        
        progressIndicator.setVisible(false);
        progressLayout.addComponent(progressIndicator);
        progressLayout.addComponent(cancelProcessing);
        uploadDetailsPanelLayout.addComponent(progressLayout);
        
        fileName.setCaption("File name");
     
        uploadDetailsPanelLayout.addComponent(fileName);
      
        textualProgress.setVisible(false);
        uploadDetailsPanelLayout.addComponent(textualProgress);

        addComponent(uploadDetailsPanel);
        
        upload.addListener(new Upload.StartedListener() {

            public void uploadStarted(StartedEvent event) {
                // this method gets called immediatedly after upload is
                // started
                progressIndicator.setValue(0f);
                progressIndicator.setVisible(true);
                progressIndicator.setPollingInterval(500); // hit server frequantly to get
                textualProgress.setVisible(true);
                // updates to client
                fileName.setValue(event.getFilename());

                cancelProcessing.setVisible(true);
            }
        });

        upload.addListener(new Upload.ProgressListener() {

            public void updateProgress(long readBytes, long contentLength) {
                // this method gets called several times during the update
                progressIndicator.setValue(new Float(readBytes / (float) contentLength));
                textualProgress.setValue("Processed " + readBytes
                        + " bytes of " + contentLength);
            }
        });

        upload.addListener(new Upload.SucceededListener() {

            public void uploadSucceeded(SucceededEvent event) {
                
            }
        });

//        upload.addListener(new Upload.FailedListener() {
//
//            public void uploadFailed(FailedEvent event) {
//                result.setValue(counter.getLineBreakCount()
//                        + " (counting interrupted at "
//                        + Math.round(100 * (Float) pi.getValue()) + "%)");
//            }
//        });

        upload.addListener(new Upload.FinishedListener() {

            public void uploadFinished(FinishedEvent event) {
                String mimetype = new MimetypesFileTypeMap().getContentType(counter.getUploadedFile());
                String type = mimetype.split("/")[0];
                if (type.equals("image")) {
                    try {
                        Image inputImage = ImageIO.read(counter.getUploadedFile());
                        ImageNudityDetector nudityDetector = new ColoredImageNudityDetector();
                        boolean isNude = nudityDetector.isImageNude(inputImage);
                        System.out.println("file: " + counter.getUploadedFile().getName() + ", isNude: " + isNude);
                    } catch (IOException e) {}
                }
                else {
                    VideoFrameExtractor frameExtractor = new VideoFrameExtractorImpl();
                        frameExtractor.extractFrames(counter.getUploadedFile().getAbsolutePath(), "C:/samples/Test/frames/");
                        File folder = new File("C:/samples/Test/frames/");
                        File[] frames = folder.listFiles();
                        for (File frame : frames) {
                            try{
                            Image inputImage = ImageIO.read(frame);
                            ImageNudityDetector nudityDetector = new ColoredImageNudityDetector();
                            boolean isNude = nudityDetector.isImageNude(inputImage);
                            System.out.println("file: " + frame.getName() + ", isNude: " + isNude);
                            }catch(IOException e) {}
                        }
                }
                        
                progressIndicator.setVisible(false);
                textualProgress.setVisible(false);
                cancelProcessing.setVisible(false);
            }
        });

    }

    public static class LineBreakCounter implements Receiver {

        private File uploadedFile = null;
        private String fileName;
        private String mtype;
        /**
         * return an OutputStream that simply counts line ends
         */
        public OutputStream receiveUpload(String filename, String MIMEType) {
            fileName = filename;
            mtype = MIMEType;
            uploadedFile = new File("C:/samples/Test/" + fileName);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(uploadedFile);
            }catch(IOException e){}
            return fos;
        }

        public String getFileName() {
            return fileName;
        }

        public String getMimeType() {
            return mtype;
        }
        
        public File getUploadedFile() {
            return uploadedFile;
        }
    }
}