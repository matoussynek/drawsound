![Logo](src/resources/icon.png)
### DrawSound - *MIDI instrument based on an image output*

>“All sounds of the Earth are like music.” Oscar Hammerstein

This is a MIDI device, that can read an image input and convert it to a set of MIDI CC messages

#### Interface
It contains a simple user-friendly interface
![interface](src/resources/interface.png)
##### Mapping sector

The mapping sector is located on the left hand side. Here you can map each image characterist to any software instrument or 
DAW of your choice. you want to. This is done by simply selecting *"Learn MIDI Assignment"* for any control within your 
software instrument or DAW and then clicking a button with name of the image characteristic you want to map to this control.
When the button turns blue, the mapping is in progress and DrawSound is sending mapping data to your instrument. Once you see 
in the instrument that mapping was done, click the button again to turn off the mapping. The button turns grey again.

The sliders and labels show the current value that is being sent in a MIDI CC message of corresponding characteristics.

![mapping sector](src/resources/mapping.png)
##### Image view sector

The image view sector is located in the center of the application and serves the purpose of loading and displaying the input image. 
Simply click the *"Select image..."* button to open the file chooser and select some *.jpg or *.png file. The file path 
to the image is displayed in the text box next to the button and the image right below.

![mapping sector](src/resources/imageview.png)
##### Effects sector
The effects sector is located on the right hand side. With *"Red filter"*, *"Green filter"* and *"Blue filter"* you can 
filter out corresponding color from the picture. Sliders allow for adjusting the strenght of each effect. 0 means no effect 
applied, 100 is full strength of the effect.

The *"Greyscale"* button turns the image into greyscale. The slider allows for adjusting the strenght of the effect. 0 means 
no effect applied, 100 is full strength of the effect.

The *"Edges"* button previews the output of the edge dection algorithm, that is used as one of the image characteristics. 
The slider next to the button allows you to set the strength of the edge detection. Higher it is, bigger must be the color 
difference between two pixels to create an edge.

The *"Original"* button restores the displayed image back to the original and resets the effects.

The *"Process"* button applies the effects and recalculates the image characteristics.

![mapping sector](src/resources/effects.png)

#### Setup
You can either compile the code yourself or use the .jar file located in *out/artifacts/DrawSoundFX_jar*