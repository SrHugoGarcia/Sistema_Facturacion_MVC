package org.hugo.springboot.app.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.hugo.springboot.app.dao.IClienteDao;
import org.hugo.springboot.app.models.entity.Cliente;
import org.hugo.springboot.app.services.IClienteService;
import org.hugo.springboot.app.services.IUploadFileService;
import org.hugo.springboot.app.util.paginator.PageRender;
import org.hugo.springboot.app.view.xml.ClienteList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private IClienteService clienteService;
    private final static String UPLOADS_FOLDER = "uploads";
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private IUploadFileService uploadFileService;

    @RequestMapping(value = {"/listar","/"}, method = RequestMethod.GET)
    public String listar(@RequestParam(name="page",defaultValue = "0") int page, Model model,
                         Authentication authentication,HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            log.info("Hola usuario autenticado, usando la forma estatica SecurityContextHolder.getContext().getAuthentication(), tu username es: ".concat(auth.getName()));
        }
        if(hasRole("ROLE_ADMIN")){
            logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso"));
        }else{
            logger.info("Hola ".concat(auth.getName()).concat(" No tienes acceso"));
        }
        SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
        if(securityContext.isUserInRole("ADMIN")){
            logger.info("Forma usando SecurityContextHolderAwareRequestWrapper hola".concat(auth.getName()).concat(" tu username es: " + auth.getName()));
        }else{
            logger.info("Forma usando SecurityContextHolderAwareRequestWrapper Hola ".concat(auth.getName()).concat(" No tienes acceso"));
        }

        if(request.isUserInRole("ROLE_ADMIN")){
            logger.info("Forma usando HttpServletRequest Hola".concat(auth.getName()).concat(" tu username es: " + auth.getName()));
        }else{
            logger.info("Forma usando SecurityContextHolderAwareRequestWrapper Hola ".concat(auth.getName()).concat(" No tienes acceso"));
        }
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Cliente> clientes = clienteService.findAll(pageRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/listar",clientes);
        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return "listar";
    }
    @RequestMapping(value = {"/listar-res"}, method = RequestMethod.GET)
    public @ResponseBody ClienteList listarRest() {
        //return clienteService.findAll();
        return new ClienteList(clienteService.findAll());
    }

    @GetMapping("/form")
    public String crear(Map<String, Object> model) {
        model.put("titulo", "Formulario del Cliente");
        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result, Model model,
                          @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Formulario del Cliente");
            return "form";
        }
        if(!foto.isEmpty()){
            if(cliente.getId() != null && cliente.getId() >0 && cliente.getFoto() != null && cliente.getFoto().length()>0){
                uploadFileService.delete(cliente.getFoto());
            }
            String uniqueFileName = null;
            try {
                uniqueFileName = uploadFileService.copy(foto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            flash.addFlashAttribute("info","Has subido correctamente " + uniqueFileName);
            cliente.setFoto(uniqueFileName);
        }
        String mensajeFlash =(cliente.getId() !=null)?"Cliente editado con exito":"Cliente creado con exito";
        clienteService.createOne(cliente);
        status.setComplete();
        flash.addFlashAttribute("success",mensajeFlash);
        return "redirect:listar";
    }

    @GetMapping("/form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model,RedirectAttributes flash) {
        Cliente cliente = null;
        if (id > 0) {
            cliente = clienteService.findById(id);
            if(cliente == null){
                flash.addFlashAttribute("error","El ID del cliente no existe");
                return "redirect:/listar";
            }
        }else{
            flash.addFlashAttribute("error","El ID del cliente no puede ser cero");
            return "redirect:/listar";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Editar cliente");
        return "form";
    }
    @RequestMapping(value = "/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id,RedirectAttributes flash){
        if(id>0){
            Cliente cliente = clienteService.findById(id);

            clienteService.deleteOne(id);
            flash.addFlashAttribute("success","Cliente eliminado con exito");

            if(uploadFileService.delete(cliente.getFoto())){
                flash.addFlashAttribute("info","Foto " + cliente.getFoto() + " eliminada con exito");
            }
        }
        return "redirect:/listar";
    }

    //@Secured({"ROLE_ADMIN","ROLE_USER"}) autenticacion atraves de anotaciones
    //@PreAuthorize("hasRole('ROLE_ADMIN')") autenticacion atraves de anotaciones
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')") autenticacion atraves de anotaciones
    @GetMapping(value = "/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id,Map<String,Object> model,RedirectAttributes flash){
        Cliente cliente = clienteService.fetchByIdWithFacturas(id);// clienteService.findById(id);
        if(cliente == null){
            flash.addFlashAttribute("error","El cliente no existe en la base de datos");
            return "redirect:/listar";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Detalle cliente: " + cliente.getNombre());
        return "ver";
    }
    //el .+ admite el .jpeg etc para que no lo quite
    @GetMapping(value = "/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename){
        Resource recurso = null;
        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
           e.printStackTrace();
        }
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""+ recurso.getFilename() + "\"").body(recurso);
    }

    private boolean hasRole(String role){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if(securityContext == null){
            return false;
        }
        Authentication authentication = securityContext.getAuthentication();
        if(authentication == null){
            return false;
        }
        //Cualquier clase role implementa esta interfaz
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        /*for (GrantedAuthority authority: authorities){
            if(role.equalsIgnoreCase(authority.getAuthority())){
                logger.info("Hola usuario "
                        .concat(authentication.getName())
                        .concat( " tu role es: ")
                        .concat(authority.getAuthority()));
                return true;
            }
        }
        return false;*/
        return authorities.contains(new SimpleGrantedAuthority(role));
    }
}
