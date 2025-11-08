package com.joseluisgs.walaspringboot.controllers;

import com.joseluisgs.walaspringboot.models.User;
import com.joseluisgs.walaspringboot.services.UserService;
import com.joseluisgs.walaspringboot.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/app/perfil")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public String showProfile(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.buscarPorEmail(email);
        model.addAttribute("usuario", user);
        return "app/perfil";
    }

    @PostMapping("/editar")
    public String updateProfile(@Valid @ModelAttribute("usuario") User updatedUser,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                BindingResult bindingResult,
                                Model model) {
        
        if (bindingResult.hasErrors()) {
            return "app/perfil";
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User existingUser = userService.buscarPorEmail(email);

        // Update only allowed fields
        existingUser.setNombre(updatedUser.getNombre());
        existingUser.setApellidos(updatedUser.getApellidos());

        // Handle avatar upload
        if (file != null && !file.isEmpty()) {
            // Delete old avatar if exists
            if (existingUser.getAvatar() != null && !existingUser.getAvatar().isEmpty()) {
                storageService.delete(existingUser.getAvatar());
            }
            // Upload new avatar
            String avatar = storageService.store(file);
            existingUser.setAvatar(MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "serveFile", avatar).build().toUriString());
        }

        userService.editar(existingUser);
        model.addAttribute("mensaje", "Perfil actualizado correctamente");
        model.addAttribute("usuario", existingUser);
        
        return "app/perfil";
    }
}
