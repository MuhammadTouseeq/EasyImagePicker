# EasyImagePicker

[![](https://jitpack.io/v/MuhammadTouseeq/EasyImagePicker.svg)](https://jitpack.io/#MuhammadTouseeq/EasyImagePicker)

EasyImagePicker allow us to pick image from camera or gallery without creating a lot of boilerplate code .it support Android 10 version



## Features built in:

1. Runtime Permissions for camera and Gallery built in
2. Camera Integration
3. Less line of code to implement this 
4. File Provider
5. Custom Picker Dialog 
6. Dialog animations
7. Open File picker

## Screenshots
<img src='screenshots/permissions.gif' height=480  width=240/> <img src='screenshots/take_picture.gif' height=480  width=240/>


## Integration 

Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### implementation
Just add it to your dependencies
```
 implementation 'com.github.MuhammadTouseeq:EasyImagePicker:v1.0.3'
```
### For Dexture (Optional)

``` implementation 'com.karumi:dexter:4.2.0'```

## Usage

### How to implement in App
  
  ```
  
     EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID).openEasyPicker();

     @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImagePicker.getInstance().passActivityResult(requestCode, resultCode, data, new EasyImagePicker.easyPickerCallback() {
            @Override
            public void onMediaFilePicked(String result) {

            }
            @Override
            public void onFailed(String error) {

                Toast.makeText(MainActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
 ```

### For Open Camera

```
EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID).openCamera();
```

### For Open Gallery

```
  EasyImagePicker.getInstance().withContext(this, BuildConfig.APPLICATION_ID).openGallery();
```

### Add in Android manifest 

```
 <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_path" />
        </provider>
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
