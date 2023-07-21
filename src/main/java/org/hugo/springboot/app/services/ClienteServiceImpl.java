package org.hugo.springboot.app.services;

import org.hugo.springboot.app.dao.IClienteDao;
import org.hugo.springboot.app.dao.IFacturaDao;
import org.hugo.springboot.app.dao.IProductoDao;
import org.hugo.springboot.app.models.entity.Cliente;
import org.hugo.springboot.app.models.entity.Factura;
import org.hugo.springboot.app.models.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService{
    @Autowired
    private IClienteDao clienteDao;

    @Autowired
    private IProductoDao productoDao;
    @Autowired
    private IFacturaDao facturaDao;
    @Transactional(readOnly = true)
    @Override
    public List<Cliente> findAll() {
        return (List<Cliente>) clienteDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Cliente findById(Long id) {
        return clienteDao.findById(id).orElse(null);
    }
    @Transactional
    @Override
    public Cliente createOne(Cliente cliente) {
        return clienteDao.save(cliente);
    }


    @Transactional
    @Override
    public void deleteOne(Long id) {
        clienteDao.deleteById(id);
    }

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteDao.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Producto> findByNombre(String term) {
        return productoDao.findByNombreLikeIgnoreCase("%"+term+"%");
    }

    @Transactional
    @Override
    public void saveFactura(Factura factura) {
        this.facturaDao.save(factura);
    }

    @Transactional(readOnly = true)
    @Override
    public Producto findByProductoById(Long id) {
        return productoDao.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Factura findFacturaById(Long id) {
        return facturaDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void deleteFactura(Long id) {
        this.facturaDao.deleteById(id);
    }

    @Override
    public Factura fetchFacturaByIdWithClienteWithItemFacturaWithProducto(Long id) {
        return this.facturaDao.fetchByIdWithClienteWithItemFacturaWithProducto(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Cliente fetchByIdWithFacturas(Long id) {
        return this.clienteDao.fetchByIdWithFacturas(id);
    }
}
