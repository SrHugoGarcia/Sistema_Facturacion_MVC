package org.hugo.springboot.app.services;

import org.hugo.springboot.app.models.entity.Cliente;
import org.hugo.springboot.app.models.entity.Factura;
import org.hugo.springboot.app.models.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteService {
    List<Cliente> findAll();
    Cliente findById(Long id);
    Cliente createOne(Cliente cliente);
    void deleteOne(Long id);
    Page<Cliente> findAll(Pageable pageable);

    List<Producto> findByNombre(String term);
    void saveFactura(Factura factura);
    Producto findByProductoById(Long id);
    Factura findFacturaById(Long id);
    void deleteFactura(Long id);
    Factura fetchFacturaByIdWithClienteWithItemFacturaWithProducto(Long id);
    Cliente fetchByIdWithFacturas(Long id);
}
