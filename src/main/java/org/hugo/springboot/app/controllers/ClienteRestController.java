package org.hugo.springboot.app.controllers;

import org.hugo.springboot.app.services.IClienteService;
import org.hugo.springboot.app.view.xml.ClienteList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteRestController {
    @Autowired
    private IClienteService clienteService;

    @RequestMapping(value = {"/listar"}, method = RequestMethod.GET)
    public ClienteList listar() {
        return new ClienteList(clienteService.findAll());
    }
}
