package it.progetto.backend.services;
/*
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    @Async
    public void inviaNotificaAggiornamento(String emailDestinatario, String nomeUtente) {
        try {
            SimpleMailMessage messaggio = new SimpleMailMessage();
            messaggio.setTo(emailDestinatario);
            messaggio.setSubject("Aggiornamento Profilo - DVD Store");
            messaggio.setText("Ciao " + nomeUtente + ",\nI dati del tuo profilo sono stati aggiornati con successo.");

            mailSender.send(messaggio);
        } catch (Exception e) {
            System.err.println("Errore logico di rete: Impossibile inviare l'email a " + emailDestinatario);
        }
    }
}
*/