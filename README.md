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
* Allows to recursively copy all elements from a folder to another by their ids.
* Allows creation of a folder with a name and parent folder id.
* Gets a list of files inside a folder. Only first level children.
* Find a folder by name inside a parent folder.
* Copy full folder structure without files.
* Get a list of files inside a folder by its MimeType.
* Get file's name by its URL.
    
## To-Do
* Read responses to control execution.

## Versions
### 0.2.0
* Allows to copy a full folder structure.
* Add method to get files in a folder given its MimeType.
* Get file's name by its URL.
### 0.1.0
* Allows to recursively copy all elements from a folder to another by their ids.
* Allows creation of a folder with a name and parent folder id.
* Gets a list of files inside a folder. Only first level children.
* Find a folder by name inside a parent folder.