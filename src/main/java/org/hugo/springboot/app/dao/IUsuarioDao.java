package org.hugo.springboot.app.dao;

import org.hugo.springboot.app.models.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {
    Usuario findByUsername(String username);
}
