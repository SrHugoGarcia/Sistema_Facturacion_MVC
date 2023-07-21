package org.hugo.springboot.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(@RequestParam(value = "error",required = false) String error,
                        Model model, Principal principal, RedirectAttributes flash,
                        @RequestParam(value = "logout",required = false) String logout){

        if(error != null){
            model.addAttribute("error", "Error nombre de usuario o contraseña incorrecta. Porfavor vuelva a intentarlo");
        }
        if(principal != null){
            flash.addFlashAttribute("info","Ya ha iniciado sesion anteriormente");
            return "redirect:/";
        }
        if(logout != null){
            model.addAttribute("success", "cerrado sesion con exito");
        }
        return "login";
    }
}
