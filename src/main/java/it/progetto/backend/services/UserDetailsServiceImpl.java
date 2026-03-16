package it.progetto.backend.services;
import it.progetto.backend.entities.Cliente;
import it.progetto.backend.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + cliente.getRuolo().name());

        return new User(
                cliente.getEmail(),
                cliente.getPassword(),
                Collections.singletonList(authority) //assegno i poteri tradotti
        );
    }
}
