package drawsoundfx.utils;

import javafx.scene.control.Button;
import javafx.util.Pair;
import rwmidi.MidiOutput;
import rwmidi.MidiOutputDevice;
import rwmidi.RWMidi;

import java.awt.image.BufferedImage;
import java.util.*;

public class DrawSound {

    private TeVirtualMIDIPort midiPort;
    private final String MIDI_PORT_NAME = "drawSoundMidiPort";
    private MidiOutput device;
    private MidiOutput defaultDevice;
    private boolean mapping = false;
    private String currentCistic = null;
    private MappingThread mt;
    private Thread mappingThread;
    private List<String> outputDevices = new ArrayList<>();
    private MidiOutputDevice[] devices;

    private int rChannelCC = 0;
    private int gChannelCC = 0;
    private int bChannelCC = 0;
    private int brightnessCC = 0;
    private int colorDiversityCC = 0;
    private int edgesCC = 0;

    private int[][][] colorHistogram;
    private Set<Integer> uniqueColsSet = new HashSet<>();
    private double edgeStrength = 0.0;
    private Map<String, Pair<Integer,Integer>> ccMapping = new HashMap<>();

    private EdgeDetection detector = new EdgeDetection();

    public DrawSound() {
        ccMapping.put("red", new Pair<>(80,1));
        ccMapping.put("green", new Pair<>(81,1));
        ccMapping.put("blue", new Pair<>(82,1));
        ccMapping.put("brightness", new Pair<>(83,1));
        ccMapping.put("diversity", new Pair<>(84,1));
        ccMapping.put("edges", new Pair<>(85,1));
    }

    /**
     * Initializing the MIDI connection
     * Creating the MIDI port called MIDI_PORT_NAME
     * Selecting this port as the output of the application
     */
    public void initialize() {

        midiPort = new TeVirtualMIDIPort(MIDI_PORT_NAME);
        midiPort.start();

        System.out.println("Virtual MIDI port created as: " + MIDI_PORT_NAME);

        devices = RWMidi.getOutputDevices();
        for (int i = 0; i < devices.length; i++) {
            if (devices[i].getName().contains(MIDI_PORT_NAME)) {
                device = RWMidi.getOutputDevices()[i].createOutput();
                defaultDevice = RWMidi.getOutputDevices()[i].createOutput();
                System.out.println("Connected to: " + devices[i].getName());
            }
            else{
                outputDevices.add(devices[i].getName());
            }
        }
    }

    /**
     * Mapping a characteristic to a host device
     * @param cstic Image characteristic
     * @param button Button that triggered the action
     */
    public void mapCC(String cstic, Button button) {

        if (cstic.equals(currentCistic) || currentCistic == null) {
            if (mapping) {
                mt.stop();
                mapping = false;
                button.setStyle("-fx-background-color: #CECECE!important;-fx-text-fill:  #585858;");
                currentCistic = null;
            } else {
                currentCistic = cstic;
                mt = new MappingThread(device, ccMapping.get(cstic).getValue(), ccMapping.get(cstic).getKey());
                mappingThread = new Thread(mt);
                mappingThread.start();
                mapping = true;
                button.setStyle("-fx-background-color: #546a7b!important;-fx-text-fill:  #DEDEDE;");

            }
        }

    }

    /**
     * Send a CC Message on the output of MIDI device
     * @param CCNumber Controller number
     * @param intValue Message value
     */
    public void sendMessage(int CCNumber, int intValue) {

        device.sendController(1, CCNumber, intValue);
    }

    /**
     * Perform the edge detection
     * @param displayedImage Input image
     * @return Result of the edge detection
     */
    public BufferedImage getEdgesImage(BufferedImage displayedImage) {
        detector.setSourceImage(displayedImage);
        detector.setDistance(edgeStrength);
        detector.detectEdges();
        return detector.getOutputImage();
    }

    public void processImage(BufferedImage image) {
        createHistogram(image);
    }

    /**
     * Creates the color histogram and updates the values of Image characteristics
     * @param image Input image
     */
    private void createHistogram(BufferedImage image) {
        int brightnessSum = 0;
        colorHistogram = new int[4][4][4];
        rChannelCC = 0;
        gChannelCC = 0;
        bChannelCC = 0;
        uniqueColsSet = new HashSet<>();
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {
                int color = image.getRGB(x, y);
                uniqueColsSet.add(color);
                int alpha = (color & 0xff000000) >> 24;
                int red = (color & 0x00ff0000) >> 16;
                int green = (color & 0x0000ff00) >> 8;
                int blue = color & 0x000000ff;
                colorHistogram[red / 64][green / 64][blue / 64]++;
                int grayscalePixel = (int) ((0.21 * red) + (0.71 * green) + (0.07 * blue));
                brightnessSum += grayscalePixel;
            }
        for (int i = 0; i < colorHistogram.length; i++)
            for (int j = 0; j < colorHistogram[i].length; j++)
                for (int p = 0; p < colorHistogram[i][j].length; p++) {
                    rChannelCC += i * colorHistogram[i][j][p];
                    gChannelCC += j * colorHistogram[i][j][p];
                    bChannelCC += p * colorHistogram[i][j][p];
                }
        int colorSum = rChannelCC + gChannelCC + bChannelCC;
        rChannelCC = (int) map(rChannelCC, 0, colorSum, 0, 127);
        gChannelCC = (int) map(gChannelCC, 0, colorSum, 0, 127);
        bChannelCC = (int) map(bChannelCC, 0, colorSum, 0, 127);
        brightnessCC = (int) map((brightnessSum / (image.getWidth() * image.getHeight())), 0, 255, 0, 127);
        colorDiversityCC = (int) map(uniqueColsSet.size(), 0, (image.getWidth() * image.getHeight()), 0, 127);
        detector.setDistance(edgeStrength);
        detector.setSourceImage(image);
        detector.detectEdges();
        edgesCC = (int) map(detector.getEdges(), 0, (image.getWidth() * image.getHeight()), 0, 127);
    }

    private float map(float var, float iMin, float iMax, float oMin, float oMax) {
        return oMin + (oMax - oMin) * ((var - iMin) / (iMax - iMin));
    }


    public List<String> getOutputDevices() {
        return outputDevices;
    }

    /**
     * Sets a new existing MIDI device
     * @param outputName Device name
     */
    public void setExternalMidiOutput(String outputName){
        for (int i = 0; i < devices.length; i++) {
            if (devices[i].getName().contains(outputName)) {
                device = RWMidi.getOutputDevices()[i].createOutput();
                System.out.println("Connected to: " + devices[i].getName());
            }
        }
    }
    public void setDefaultMidiOutput(){
        device = defaultDevice;
        System.out.println("Connected to: " + device.getName());
    }

    public int getrChannelCC() {
        return rChannelCC;
    }

    public int getgChannelCC() {
        return gChannelCC;
    }

    public int getbChannelCC() {
        return bChannelCC;
    }

    public int getBrightnessCC() {
        return brightnessCC;
    }

    public int getColorDiversityCC() {
        return colorDiversityCC;
    }

    public int getEdgesCC() {
        return edgesCC;
    }

    public void setEdgeStrength(double edgeStrength) {
        this.edgeStrength = edgeStrength;
    }

    public EdgeDetection getDetector() {
        return detector;
    }

    public TeVirtualMIDIPort getMidiPort() {
        return midiPort;
    }

    public String getOutputDeviceString() {
        if (device.getName().contains(MIDI_PORT_NAME))
            return null;
        return device.getName();
    }

    /**
     * Sets Controller numbers and Channel numbers for Image characteristics
     * @param rn
     * @param rc
     * @param gn
     * @param gc
     * @param bn
     * @param bc
     * @param brn
     * @param brc
     * @param dn
     * @param dc
     * @param en
     * @param ec
     */
    public void setCcMapping(int rn, int rc, int gn, int gc, int bn, int bc, int brn, int brc, int dn, int dc, int en, int ec) {
        ccMapping.replace("red", new Pair<>(rn,rc));
        ccMapping.replace("green", new Pair<>(gn,gc));
        ccMapping.replace("blue", new Pair<>(bn,bc));
        ccMapping.replace("brightness", new Pair<>(brn,brc));
        ccMapping.replace("diversity", new Pair<>(dn,dc));
        ccMapping.replace("edges", new Pair<>(en,ec));
    }
    public Map<String, Pair<Integer, Integer>> getCcMapping(){
        return ccMapping;
    }
}
