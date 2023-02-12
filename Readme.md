# Test task

Using the stackoverflow.com api documented at: https://api.stackexchange.com/docs retrieve the list of stack overflow users meeting the following criteria:

    i. Are located in Romania or Moldova
    ii. Have a reputation of min 223 points.
    iii. Answered min 1 question
    iv. Have the tags: "java",".net","docker" or "C#"

Requirements:

The code should be tracked with git and pushed to Github or Bitbucket. 
The app should be written in Java (bonus points if written in Kotlin)
The list of retrieved users should be dumped in a list in STDOUT. 

Each line should contain:

    User name
    Location
    Answer count
    Question count
    Tags as a comma delimited string
    Link to profile
    Link to avatar

Bonus points for:

    Writing the app in Kotlin
    Using gradle as a package manager.
    Use retrofit (https://square.github.io/retrofit/) for API interaction.



## Manual

### Stackoverflow registration

In order to use StackOverflow's API with extended features: API calls limit per day = 10000; pages visible = above 25.

1. You need to register you app as described here

   
   https://stackapps.com/questions/67/how-api-keys-work-faq


2. You need to obtain the API-key


      https://stackapps.com/apps/oauth/register

3. You need to put an API-key to system variables
    

       key=...

4. In order to allow debug messages, add to VM options 

   
      -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG

5. In order to disable debug messages, add to VM options


      -Dorg.slf4j.simpleLogger.defaultLogLevel=ERROR

6. config.properties file is used to set up application properties


      Query details
      Request parameters
   