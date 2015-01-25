package com.dimafeng.craigslist

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import javax.mail.*
import javax.mail.internet.*

public class Main {
    public static void main(String[] args) {

        println "App started " + new Date()

        //Reading file with old ads
        def old = new ArrayList()
        def file = new File('old')
        def firstStart = false
        if (!file.exists()) {
            file.createNewFile();
            firstStart = true;
        } else {
            old.addAll(file.readLines())
        }

        //Config parsing
        def config = new Properties()
        config.load(new File('app.properties').newDataInputStream())

        def targetUrl = config.getProperty("app.url")
        def baseUrl = targetUrl.replaceAll("(http://(.*?).craigslist.org)(.*)", '$1');

        Document doc = Jsoup.connect(targetUrl).get();
        Elements ads = doc.select("p.row");

        def result = new StringBuilder();
        int i = 0;

        ads.forEach({ e ->
            def id = e.attr("data-pid").toString()
            def url = e.getElementsByTag("a").first().attr("href")

            if (!old.contains(id)) {
                old.add(id)
                try {
                    i++;

                    if (i < 6) {
                        def link = baseUrl + url
                        Document doc2 = Jsoup.connect(link).get();
                        Elements details = doc2.select("section.body")

                        result.append("<a href=\"" + link + "\"><h2>Visit</h2></a>")
                        result.append(details.toString());
                        result.append("<br><br><br>");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace()
                }
            }
        })

        // Send e-mails
        if (result.size() > 0 && !firstStart) {
            def emails = config.getProperty("app.recipients").split(",");
            emails.each {
                simpleMail(config.getProperty("app.gmail.email"),
                    config.getProperty("app.gmail.password"),
                    it,
                    "${i} ads found",
                    result.toString()
                );
            }
        }

        // Append new adds to file
        def writer = new PrintWriter(file)
        old.each { id -> writer.println(id) }
        writer.close()
    }

    public static void simpleMail(String from, String password, String to,
                                  String subject, String body) throws Exception {

        String host = "smtp.gmail.com";
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true);
        props.setProperty("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));

        InternetAddress toAddress = new InternetAddress(to);

        message.addRecipient(Message.RecipientType.TO, toAddress);

        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");

        Transport transport = session.getTransport("smtp");

        transport.connect(host, from, password);

        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}