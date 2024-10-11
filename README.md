# deepl-document-translator

The purpose of this project was to demonstrate the issue with accessing the DeepL document translation endpoint
using a default JDK HttpClient. While the request was constructed correctly, the transferred file was reported 
to be invalid.

The root cause was an invalid multipart/form-data boundary string that exceeded the maximum length per spec.
While some servers can handle this, DeepL is strict. The boundary string length was reduced and now it works fine.

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
