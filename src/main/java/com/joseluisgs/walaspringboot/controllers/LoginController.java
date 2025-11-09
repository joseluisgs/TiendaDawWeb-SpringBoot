package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.UserService;
import com.joseluisgs.walaspringboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
public class LoginController {

    final UserService usuarioServicio;
    final StorageService storageService;

    @Autowired
    public LoginController(UserService usuarioServicio, StorageService storageService) {
        this.usuarioServicio = usuarioServicio;
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String welcome() {
        return "redirect:/public/";
    }

    @GetMapping("/auth/login")
    public String login(Model model) {
        // CSRF token is handled by GlobalControllerAdvice
        // Para el formulario de registro
        model.addAttribute("usuario", new User());
        return "login";
    }

    @PostMapping("/auth/register")
    public String register(@ModelAttribute User usuario,
                           @RequestParam("file") MultipartFile file) {
        // Subida de imágenes
        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            usuario.setAvatar(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", imagen)
                    .build().toUriString());
        }

        usuarioServicio.registrar(usuario);
        return "redirect:/auth/login";
    }
}
