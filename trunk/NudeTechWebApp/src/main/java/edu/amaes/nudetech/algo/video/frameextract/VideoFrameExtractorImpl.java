package edu.amaes.nudetech.algo.video.frameextract;

import edu.amaes.nudetech.algo.video.frameextract.utilities.VideoExtractProperties;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

public class VideoFrameExtractorImpl implements VideoFrameExtractor {

    private static final Logger LOGGER = Logger.getLogger(VideoFrameExtractorImpl.class);
    private static final String FILE_DOT = ".";
    private static final double DEFAULT_SECONDS_BETWEEN_FRAMES = 0.5;
    private double secondsBetweenFrames;
    private long nanoSecondsBetweenFrames;
    private long timeLastFrameWrite;
    private int currentFrameNo;
    private String frameName;
    private String outputDirectory;
    private String outputFileType;
    private String[] supportedVideoFileTypes;

    public VideoFrameExtractorImpl() {
        init();
    }

    private void createDirectory(String outputDirectory) {
        File outputDir = new File(outputDirectory);
        outputDir.mkdirs();
    }

    private void init() {
        currentFrameNo = 0;
        timeLastFrameWrite = Global.NO_PTS;
        outputFileType = VideoExtractProperties.getString("output.file.type");
        initializeSupportedVideoFileTypes();
        initializeSecondsBetweenFrames();
    }

    private void initializeSupportedVideoFileTypes() {
        supportedVideoFileTypes = new String[0];

        String fileTypes = VideoExtractProperties.getString("supported.file.types");

        if (fileTypes != null && !fileTypes.isEmpty()) {
            supportedVideoFileTypes = fileTypes.split(",");
        }
    }

    private void initializeSecondsBetweenFrames() {
        try {
            String secBetFrames = VideoExtractProperties.getString("seconds.between.frames");
            secondsBetweenFrames = Double.parseDouble(secBetFrames);
        } catch (Exception ex) {
            secondsBetweenFrames = DEFAULT_SECONDS_BETWEEN_FRAMES;
        }
        nanoSecondsBetweenFrames = (long) (Global.DEFAULT_PTS_PER_SECOND * secondsBetweenFrames);
    }

    @Override
    public void extractFrames(String videoFilename, String outputDirectory) {
        validateVideoFile(videoFilename);

        validateOutputDirectory(outputDirectory);

        this.frameName = generateFrameName(videoFilename);
        this.outputDirectory = outputDirectory;

        checkIfCanConvertVideoPixelFormats();

        decodeVideoFile(videoFilename);
    }

    private String generateFrameName(String videoFilename) {
        File videoFile = new File(videoFilename);

        String filename = videoFile.getName();
        int mid = filename.lastIndexOf(".");
        String fname = filename.substring(0, mid);

        return fname;
    }

    private void decodeVideoFile(String videoFilename) {
        // create a Xuggler container object
        IContainer container = IContainer.make();

        // open up the container

        if (container.open(videoFilename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: "
                    + videoFilename);
        }

        // query how many streams the call to open found

        int numStreams = container.getNumStreams();

        // and iterate through the streams to find the first video stream

        int videoStreamId = -1;
        IStreamCoder videoCoder = null;
        for (int i = 0; i < numStreams; i++) {
            // find the stream object

            IStream stream = container.getStream(i);

            // get the pre-configured decoder that can decode this stream;

            IStreamCoder coder = stream.getStreamCoder();

            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }

        if (videoStreamId == -1) {
            throw new RuntimeException(
                    "could not find video stream in container: "
                    + videoFilename);
        }

        // Now we have found the video stream in this file. Let's open up
        // our decoder so it can do work

        if (videoCoder.open() < 0) {
            throw new RuntimeException(
                    "could not open video decoder for container: "
                    + videoFilename);
        }

        IVideoResampler resampler = null;
        if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
            // if this stream is not in BGR24, we're going to need to
            // convert it. The VideoResampler does that for us.

            resampler = IVideoResampler.make(videoCoder.getWidth(),
                    videoCoder.getHeight(), IPixelFormat.Type.BGR24,
                    videoCoder.getWidth(), videoCoder.getHeight(),
                    videoCoder.getPixelType());
            if (resampler == null) {
                throw new RuntimeException(
                        "could not create color space resampler for: "
                        + videoFilename);
            }
        }

        // Now, we start walking through the container looking at each packet.

        IPacket packet = IPacket.make();

        while (container.readNextPacket(packet) >= 0) {

            // Now we have a packet, let's see if it belongs to our video stream

            if (packet.getStreamIndex() == videoStreamId) {
                // We allocate a new picture to get the data out of Xuggle

                IVideoPicture picture = IVideoPicture.make(
                        videoCoder.getPixelType(), videoCoder.getWidth(),
                        videoCoder.getHeight());

                int offset = 0;

                while (offset < packet.getSize()) {
                    // Now, we decode the video, checking for any errors.

                    int bytesDecoded = videoCoder.decodeVideo(picture, packet,
                            offset);
                    if (bytesDecoded < 0) {
                        throw new RuntimeException(
                                "got error decoding video in: " + videoFilename);
                    }
                    offset += bytesDecoded;

                    // Some decoders will consume data in a packet, but will not
                    // be able to construct a full video picture yet. Therefore
                    // you should always check if you got a complete picture
                    // from
                    // the decode.

                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;

                        // If the resampler is not null, it means we didn't get
                        // the
                        // video in BGR24 format and need to convert it into
                        // BGR24
                        // format.

                        if (resampler != null) {
                            // we must resample
                            newPic = IVideoPicture.make(
                                    resampler.getOutputPixelFormat(),
                                    picture.getWidth(), picture.getHeight());
                            if (resampler.resample(newPic, picture) < 0) {
                                throw new RuntimeException(
                                        "could not resample video from: "
                                        + videoFilename);
                            }
                        }

                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            throw new RuntimeException(
                                    "could not decode video as BGR 24 bit data in: "
                                    + videoFilename);
                        }

                        // convert the BGR24 to an Java buffered image

                        BufferedImage javaImage = Utils.videoPictureToImage(newPic);

                        // process the video frame

                        processFrame(newPic, javaImage);
                    }
                }
            }
        }

        closeVideoDecoder(videoCoder);
        closeContainer(container);
    }

    private void closeContainer(IContainer container) {
        if (container != null) {
            container.close();
            container = null;
        }
    }

    private void closeVideoDecoder(IStreamCoder videoCoder) {
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
    }

    private void validateOutputDirectory(String outputDirectory) {
        if (outputDirectory == null || outputDirectory.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid directory for output frames: empty");
        }

        File outputDir = new File(outputDirectory);

        if (!outputDir.exists()) {
            createDirectory(outputDirectory);
        }

        if (!outputDir.isDirectory()) {
            throw new IllegalArgumentException(
                    "Invalid output directory: not a valid directory");
        }
    }

    private void validateVideoFile(String videoFilename) {
        if (videoFilename == null || videoFilename.isEmpty()) {
            throw new IllegalArgumentException("Invalid video file name: empty");
        }

        File videoFile = new File(videoFilename);

        if (!videoFile.exists()) {
            throw new IllegalArgumentException(
                    "Invalid video file: does not exist");
        }

        if (!videoFile.isFile()) {
            throw new IllegalArgumentException(
                    "Invalid video file: not a valid file");
        }

        if (!isFileTypeSupported(videoFilename)) {
            throw new IllegalArgumentException(
                    "Invalid video file: file type not supported");
        }
    }

    private boolean isFileTypeSupported(String videoFilename) {

        String extensionName = getExtensionName(videoFilename);

        boolean supported = checkFileType(extensionName);

        return supported;
    }

    private boolean checkFileType(String extensionName) {
        boolean supported = false;

        for (String fileType : supportedVideoFileTypes) {
            if (fileType.equalsIgnoreCase(extensionName)) {
                supported = true;
            }
        }

        return supported;
    }

    private String getExtensionName(String videoFilename) {
        int mid = videoFilename.lastIndexOf(FILE_DOT);
        String extensionName = videoFilename.substring(mid + 1,
                videoFilename.length());
        return extensionName;
    }

    private void checkIfCanConvertVideoPixelFormats() {
        if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
            throw new RuntimeException(
                    "GPL version of Xuggler (with IVideoResampler support) must be installed");
        }
    }

    private void processFrame(IVideoPicture picture, BufferedImage image) {
        try {
            if (timeLastFrameWrite == Global.NO_PTS) {
                timeLastFrameWrite = picture.getPts()
                        - nanoSecondsBetweenFrames;
            }
            if (picture.getPts() - timeLastFrameWrite >= nanoSecondsBetweenFrames) {
                writeNextFrame(image);
            }
        } catch (Exception exception) {
            LOGGER.error("Error encountered while processing frame", exception);
        }
    }

    private void writeNextFrame(BufferedImage image) throws IOException {
        String outputFilename = generateOutputFilename();
        File file = new File(outputFilename);

        LOGGER.debug("Writing extracted frame: " + outputFilename);
        ImageIO.write(image, outputFileType, file);

        currentFrameNo++;

        timeLastFrameWrite += nanoSecondsBetweenFrames;
    }

    private String generateOutputFilename() {
        StringBuffer outputFilename = new StringBuffer();

        outputFilename.append(outputDirectory);
        outputFilename.append(File.separator);
        outputFilename.append(frameName);
        outputFilename.append(currentFrameNo);
        outputFilename.append(FILE_DOT);
        outputFilename.append(outputFileType);

        return outputFilename.toString();
    }
}