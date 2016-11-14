# EmailToCalendar
Automatically reads gmail emails and places them into a google calendar.
Expects the emails to be in the following format:

Event Name
Location
Start Date/Time (RFC3339 Format)
End Date/Time  (RFC3339 Format)
Description of the event

**Please note that you will have to place your authentication jsons in the resources folder 
and name them client_secrets_GCAL and client_secrets_GMAIL. Mine are removed for obvious reasons.
Read through my comments in main to determine what fields you need to change to get the code
working for you!

