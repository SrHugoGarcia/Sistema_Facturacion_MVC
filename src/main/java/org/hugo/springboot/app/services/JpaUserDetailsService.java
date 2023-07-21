package org.hugo.springboot.app.services;

import org.hugo.springboot.app.dao.IUsuarioDao;
import org.hugo.springboot.app.models.entity.Role;
import org.hugo.springboot.app.models.entity.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {
    @Autowired
    private IUsuarioDao usuarioDao;
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByUsername(username);
        if(usuario == null){
            logger.error("error "+"No existe el usuario: " + username);
            throw  new UsernameNotFoundException("El usuario no existe");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Role role : usuario.getRoles()){
            logger.info("Role: " + role.getAuthority());
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        if(authorities.isEmpty()){
            logger.error("error " +"El usuario: " + username + "No tiene roles asignados");
            throw  new UsernameNotFoundException("El usuario: " + username + "No tiene roles asignados");
        }
        return new User(usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEnabled(),
                true, true, true, authorities);
    }
}
