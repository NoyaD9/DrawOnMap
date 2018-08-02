# DrawOnMap
Quick example on how to draw and delete shapes on GoogleMap for Android. It includes polygon clipping to be able to join and split shapes.

<img src="https://github.com/NoyaD9/DrawOnMap/blob/master/examples/drawOnMap.png?raw=true" width="240"/>

## Setup

In order to properly render the map you need to request your personal Google Maps API Key by following the instruction at [this link](https://developers.google.com/maps/documentation/android-sdk/signup).

The key can be put directly in the `AndroidManifest.xml`:

`<meta-data android:name="com.google.android.geo.API_KEY" android:value="PUT YOUR KEY HERE"/>`

or you can provide the value for debug/release in a string resource file. For example in the file `res/values/google_maps_api.xml`:

```
<resources>
  <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">PUT YOUR KEY HERE</string>
</resources>
```

## Functionalities

* Draw shapes
* Combine shapes using either union or difference
* Eraser to delete parts of shapes
* Trash to clear all the shapes

A quick demonstration:

<img src="https://raw.githubusercontent.com/NoyaD9/DrawOnMap/master/examples/drawOnMapExample.gif" width="240"/>

## Acknowledgments

Poligon clipping has been made possible thanks to this library: [clipper-java](https://github.com/lightbringer/clipper-java)

Icons have been designed by [Rimas Albert](https://github.com/rimasalb)
