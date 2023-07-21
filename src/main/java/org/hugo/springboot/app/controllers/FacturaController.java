package org.hugo.springboot.app.controllers;

import jakarta.validation.Valid;
import org.hugo.springboot.app.models.entity.Cliente;
import org.hugo.springboot.app.models.entity.Factura;
import org.hugo.springboot.app.models.entity.ItemFactura;
import org.hugo.springboot.app.models.entity.Producto;
import org.hugo.springboot.app.services.IClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {
    @Autowired
    private IClienteService clienteService;
    private final Logger log = LoggerFactory.getLogger(getClass());
    @GetMapping("/form/{id}")
    public String crear(@PathVariable(value = "id") Long clienteId, Map<String, Object> model,
                        RedirectAttributes flash){
        Cliente cliente = clienteService.findById(clienteId);
        if(cliente == null){
            flash.addFlashAttribute("error","El cliente no existe en la base de datos");
            return "redirect:/listar";
        }
        Factura factura = new Factura();
        factura.setCliente(cliente);
        model.put("titulo", "Crear Factura");
        model.put("factura",factura);
        return "factura/form";
    }

    @GetMapping(value = "/cargar-productos/{term}",produces = {"application/json"})
    public @ResponseBody List<Producto> cargarProductos(@PathVariable String term){
        return clienteService.findByNombre(term);
    }
    @PostMapping("/form")
    public String guardar(@Valid Factura factura,
                          BindingResult result,
                          Model model,
                          @RequestParam(name = "item_id[]",required = false) Long[] itemId,
                          @RequestParam(name = "cantidad[]",required = false) Integer[] cantidad,
                          RedirectAttributes flash,
                          SessionStatus status) {
            if(result.hasErrors()){
                model.addAttribute("titulo", "Crear Factura");
                return "factura/form";
            }
            if(itemId == null || itemId.length == 0){
                model.addAttribute("titulo", "Crear Factura");
                model.addAttribute("error", "Error: La factura No puede no tener líneas!");
                return "factura/form";
            }
            for(int i = 0; i< itemId.length; i++){
                Producto producto = clienteService.findByProductoById(itemId[i]);
                ItemFactura itemFactura = new ItemFactura();
                itemFactura.setProducto(producto);
                itemFactura.setCantidad(cantidad[i]);
                factura.addItemFactura(itemFactura);
                log.info("ID: " + itemId[i].toString() + " cantidad: " + cantidad[i].toString());
            }

        clienteService.saveFactura(factura);
        status.setComplete();
        flash.addFlashAttribute("success","Factura creada con exito!");
        return "redirect:/ver/" + factura.getCliente().getId();
    }
    @GetMapping("/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id,
                      Model model,
                      RedirectAttributes flash){
        Factura factura =clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id); //clienteService.findFacturaById(id);
        if(factura==null){
            flash.addFlashAttribute("error","La factura no existe en la base de datos!");
            return "redirect:/listar";
        }
        model.addAttribute("factura", factura);
        model.addAttribute("titulo", "Factura : ".concat(factura.getDescripcion()));
        return "factura/ver";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash){
        Factura factura = clienteService.findFacturaById(id);
        if(factura != null){
            clienteService.deleteFactura(id);
            flash.addFlashAttribute("success","Factura eliminada con exito!");
            return "redirect:/ver/" + factura.getCliente().getId();
        }
        flash.addFlashAttribute("error","La factura no existe en la base de datos, no se pudo eliminar!");
        return "redirect:/listar";
    }
}
