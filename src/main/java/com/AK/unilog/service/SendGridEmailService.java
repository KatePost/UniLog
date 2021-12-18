package com.AK.unilog.service;

import com.AK.unilog.entity.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService{

    private final SendGrid sendGrid;

    public SendGridEmailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    public void sendRecoveryMail(User user, String link) {
        Email from = new Email("unilogak@gmail.com", "UniLog System");
        String subject = "Password reset";
        Email to = new Email(user.getEmail(), user.getFirstName());
        Content content = new Content("text/html", "test");
        Mail mail = new Mail(from, subject, to, content);

        mail.setTemplateId("d-f763e9ea34084659a9c9de3afe1055ea");
        Personalization personalization = new Personalization();
        personalization.addDynamicTemplateData("url", "<a href=" + link + ">Click Here</a>");
        personalization.addTo(to);
        mail.personalization.add(personalization);
        System.out.println(link);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = this.sendGrid.api(request);
            System.out.println(response.getBody());
            System.out.println("sent");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
