package org.hugo.springboot.app.dao;

import org.hugo.springboot.app.models.entity.Cliente;
import org.hugo.springboot.app.models.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

//solo incluye el crud
/*public interface IClienteDao extends CrudRepository<Cliente,Long> {

}*/
//Incluye el crud mas el paginate
public interface IClienteDao extends JpaRepository<Cliente, Long> {
    @Query("select c from Cliente c left join fetch c.facturas f where c.id=?1")
    Cliente fetchByIdWithFacturas(Long id);
}