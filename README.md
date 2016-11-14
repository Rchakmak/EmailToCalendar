# EmailToCalendar
Automatically reads GMail emails and places them into a google calendar.
Expects the emails to be in the following format:

Event Name   
Location  
Start Date/Time (RFC3339 Format)  
End Date/Time  (RFC3339 Format)  
Description of the event  

I set up a Google Form with the above parameters and used a third-party application to automatically
send me an email with the above data in the correct format.

**Please note that you will have to place your authentication jsons in the resources folder 
and name them client_secrets_GCAL and client_secrets_GMAIL. Mine are removed for obvious reasons.
Read through my comments in main to determine what fields you need to change to get the code
working for you!

Also, a lot of the credit goes to Google for its API. Most of the code and comments here is taken from
the sample java code Google provides on its API documentation website (i.e. https://developers.google.com/google-apps/). 
I just figured out how to put the pieces together.


