package it.progetto.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService
{
    private final JavaMailSender mailSender;

    @Async
    public void inviaNotificaAggiornamento(String emailDestinatario, String nomeUtente) {
        try {
            SimpleMailMessage messaggio = new SimpleMailMessage();
            messaggio.setTo(emailDestinatario);
            messaggio.setSubject("Aggiornamento Profilo - DVD Store");
            messaggio.setText("Ciao " + nomeUtente + ",\nI dati del tuo profilo sono stati aggiornati con successo.");
            messaggio.setFrom("rox.student26@gmail.com");

            mailSender.send(messaggio);
        } catch (Exception e) {
            System.err.println("Errore logico di rete: Impossibile inviare l'email a " + emailDestinatario + "\n" + e.getMessage());
        }
    }

    @Async
    public void inviaNotificaRimborso(String emailDestinatario, String nomeUtente, Long numeroOrdine, BigDecimal totaleRimborsato)
    {
        try {
            SimpleMailMessage messaggio = new SimpleMailMessage();
            messaggio.setTo(emailDestinatario);
            messaggio.setSubject("Conferma Rimborso Ordine #" + numeroOrdine + " - DVD Store");
            messaggio.setText("Gentile " + nomeUtente + ",\n\n" +
                    "Ti confermiamo che il tuo ordine #" + numeroOrdine + " è stato annullato.\n" +
                    "Abbiamo emesso un rimborso di €" + totaleRimborsato + " a tuo favore.\n\n" +
                    "Il riaccredito avverrà sul metodo di pagamento utilizzato entro 3-5 giorni lavorativi.\n\n" +
                    "A presto,\nIl team di DVD Store.");
            messaggio.setFrom("rox.student26@gmail.com");

            mailSender.send(messaggio);
            System.out.println("Email di rimborso inviata con successo a: " + emailDestinatario);
        } catch (Exception e) {
            System.err.println("Errore logico di rete: Impossibile inviare l'email di rimborso a " + emailDestinatario+ "\n" + e.getMessage());
        }
    }

    @Async
    public void inviaNotificaRegistrazione(String emailDestinatario, String nomeUtente)
    {
        try {
            SimpleMailMessage messaggio = new SimpleMailMessage();
            messaggio.setTo(emailDestinatario);
            messaggio.setSubject("Benvenuto su DVD Store, " + nomeUtente);
            messaggio.setText("Ciao " + nomeUtente + ",\n\n" +
                    "Grazie per esserti registrato su DVD Store! Siamo entusiasti di averti con noi.\n\n" +
                    "Ora puoi esplorare il nostro vasto catalogo di film, creare la tua lista dei preferiti e approfittare di offerte esclusive riservate ai nostri clienti.\n\n" +
                    "Se hai bisogno di assistenza o hai domande, non esitare a contattare il nostro servizio clienti. Siamo qui per aiutarti!\n\n" +
                    "Buona visione,\nIl team di DVD Store.");
            messaggio.setFrom("rox.student26@gmail.com");

            mailSender.send(messaggio);
            System.out.println("Email di benvenuto inviata con successo a: " + emailDestinatario);
        }catch (Exception e) {
            System.err.println("Errore logico di rete: Registrare l'utente " + emailDestinatario);
        }
    }

    @Async
    public void inviaNotificaSpedizione(String emailDestinatario, String nomeUtente, Long idOrdine, String indirizzoSpedizione)
    {
        try {
            SimpleMailMessage messaggio = new SimpleMailMessage();
            messaggio.setTo(emailDestinatario);
            messaggio.setSubject("Conferma Spedizione Ordine #" + idOrdine + " - DVD Store");
            messaggio.setText("Gentile " + nomeUtente + ",\n\n" +
                    "Ti confermiamo che il tuo ordine #" + idOrdine + " è stato spedito.\n" +
                    "L'indirizzo di spedizione è: " + indirizzoSpedizione + "\n\n" +
                    "Sarai notificato una volta che l'ordine arriverà a destinazione!\n\n" +
                    "A presto,\nIl team di DVD Store.");
            messaggio.setFrom("rox.student26@gmail.com");

            mailSender.send(messaggio);
            System.out.println("Email di spedizione inviata con successo a: " + emailDestinatario);
        } catch (Exception e) {
            System.err.println("Errore logico di rete: Impossibile inviare l'email di spedizione a " + emailDestinatario+ "\n" + e.getMessage());
        }
    }

    @Async
    public void inviaNotificaConsegna(String emailDestinatario, String nomeUtente, Long numeroOrdine, String indirizzoSpedizione)
    {
        try {
            SimpleMailMessage messaggio = new SimpleMailMessage();
            messaggio.setTo(emailDestinatario);
            messaggio.setSubject("Conferma Consegna Ordine #" + numeroOrdine + " - DVD Store");
            messaggio.setText("Gentile " + nomeUtente + ",\n\n" +
                    "Siamo felici di annunciarti che il tuo ordine #" + numeroOrdine + " è stato recapitato all'indirizzo: "+ indirizzoSpedizione +".\n" +
                    "Ora rilassati, siediti sul divano e goditi lo spettacolo offerto dal tuo amichevole DVD Store di quartiere!!\n\n" +
                    "Passa a trovarci presto per conoscere nuove ed emozionanti storie,\nIl team di DVD Store.");
            messaggio.setFrom("rox.student26@gmail.com");

            mailSender.send(messaggio);
            System.out.println("Email di consegna inviata con successo a: " + emailDestinatario);
        } catch (Exception e) {
            System.err.println("Errore logico di rete: Impossibile inviare l'email di consegna a " + emailDestinatario+ "\n" + e.getMessage());
        }
    }
}