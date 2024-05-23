# deepl-document-translator

The purpose of this project is to demonstrate the issue with accessing the DeepL document translation endpoint
using a default JDK HttpClient. While the request is constructed correctly, the transferred file is reported 
to be invalid.

## Test procedure

### Using pre-built app
1. Clone the repo
2. Open the console, navigate to the `target` subfolder
3. Set a new environment variable `DEEPL_AUTH_KEY`:
   ```
   SET DEEPL_AUTH_KEY=c9....:fx
   ```
4. Run the app
   ```
   java -jar translator.jar <path-to-your-document> <target-language>
   java -jar translator.jar "C:\documents\example.pptx" CS
   ```
5. There is `{"message":"Invalid file data."}` shown in the console

### Debugging in IDE
1. Clone the repo
2. Open the project in your IDE (NetBeans, IntelliJ IDEA)
3. Set up the executing environment (the `DEEPL_AUTH_KEY` environment variable and command line params) 
4. Debug the `main` class in `App.java` 
