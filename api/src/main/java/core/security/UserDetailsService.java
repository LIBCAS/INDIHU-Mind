package core.security;

public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

    UserDetails loadUserById(String id);

}
