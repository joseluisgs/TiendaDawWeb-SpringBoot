package dev.joseluisgs.walaspringboot.services;

import dev.joseluisgs.walaspringboot.models.Product;
import dev.joseluisgs.walaspringboot.models.Purchase;
import dev.joseluisgs.walaspringboot.repositories.ProductRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProductRepository productoRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void enviarEmailConfirmacionCompra(Purchase compra) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Locale locale = LocaleContextHolder.getLocale();

            helper.setFrom(fromEmail);
            helper.setTo(compra.getPropietario().getEmail());
            helper.setSubject(messageSource.getMessage("email.purchase.subject", null, locale));

            String htmlContent = construirEmailHTML(compra, locale);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email de confirmación enviado a: " + compra.getPropietario().getEmail());
        } catch (MessagingException e) {
            logger.error("Error al enviar email de confirmación: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al enviar email: " + e.getMessage());
        }
    }

    private String construirEmailHTML(Purchase compra, Locale locale) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang='").append(locale.getLanguage()).append("'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }");
        html.append(".container { max-width: 600px; margin: 20px auto; background: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 20px rgba(0,0,0,0.1); }");
        html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; margin: -20px -20px 20px -20px; }");
        html.append(".header h1 { margin: 0; font-size: 28px; }");
        html.append(".content { padding: 20px 0; }");
        html.append(".greeting { font-size: 18px; margin-bottom: 15px; }");
        html.append(".product { background: #f8f9fa; padding: 15px; margin: 10px 0; border-left: 4px solid #667eea; border-radius: 5px; }");
        html.append(".product-name { font-weight: bold; color: #667eea; font-size: 16px; }");
        html.append(".product-price { color: #28a745; font-weight: bold; margin-top: 5px; }");
        html.append(".total { background: #667eea; color: white; padding: 20px; margin: 20px 0; text-align: center; border-radius: 5px; font-size: 24px; font-weight: bold; }");
        html.append(".footer { text-align: center; color: #666; margin-top: 30px; padding-top: 20px; border-top: 2px solid #eee; font-size: 14px; }");
        html.append(".footer p { margin: 5px 0; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>").append(messageSource.getMessage("app.title", null, locale)).append("</h1>");
        html.append("</div>");
        html.append("<div class='content'>");

        String greeting = messageSource.getMessage("email.purchase.greeting",
                new Object[]{compra.getPropietario().getNombre()}, locale);
        html.append("<p class='greeting'>").append(greeting).append(",</p>");

        html.append("<p>").append(messageSource.getMessage("email.purchase.message", null, locale)).append("</p>");

        html.append("<h3>").append(messageSource.getMessage("email.purchase.products", null, locale)).append("</h3>");

        // Obtener productos de la compra
        List<Product> productos = productoRepository.findByCompra(compra);
        float total = 0;

        for (Product producto : productos) {
            html.append("<div class='product'>");
            html.append("<div class='product-name'>").append(producto.getNombre()).append("</div>");
            html.append("<div class='product-price'>").append(String.format("%.2f €", producto.getPrecio())).append("</div>");
            html.append("</div>");
            total += producto.getPrecio();
        }

        html.append("<div class='total'>");
        String totalMsg = messageSource.getMessage("email.purchase.total",
                new Object[]{String.format("%.2f", total)}, locale);
        html.append(totalMsg);
        html.append("</div>");

        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>").append(messageSource.getMessage("email.purchase.footer", null, locale)).append("</p>");
        html.append("<p>&copy; 2025 WalaSpringBoot</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
}
