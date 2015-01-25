# craigslist
Small tool that sends new ads to your email.

##Building a jar and running it

You need to have java SDK and gradle on your computer.

```
gradle clean uberjar
```

##Running

Create app.properties file within the folder of craigslist-1.0.jar with following content:

```
app.url=[craigslist search url (e.g. http://newyork.craigslist.org/search/mnh/nfa...)]
app.gmail.email=[gmail email which will be used as a sender]
app.gmail.password=[password for gmail account]
app.recipients=[email addresses of recipients separated by comma]
```

Run application

```
java -jar craigslist-1.0.jar
```

