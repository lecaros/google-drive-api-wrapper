# Google Drive Api Wrapper

This is a wrapper around the Google Drive (google-api-services-drive v3).

## Disclaimer :)
This project was started and is modified according to the needs I'm experiencing, in the hope that someone else will find it helpful.  
For any request please open an issue.

## How to use

Extend class AbstractDriveServiceWrapper and implement required methods. For example:  
```kotlin
override fun getDriveService(): Drive {
    return Drive.Builder(httpTransport, jsonFactory, loadCredential())
            .setApplicationName(applicationProperties.name).build()
}
 ```

## Current features
* Allows recursive copy of all elements from a folder to another by their ids.
* Allows creation of a folder with a name and parent folder id.
* Gets a list of files inside a folder. Only first level children.
* Find a folder by name inside a parent folder.

## Versions
### 0.1.0
* Allows recursive copy of all elements from a folder to another by their ids.
* Allows creation of a folder with a name and parent folder id.
* Gets a list of files inside a folder. Only first level children.
* Find a folder by name inside a parent folder.