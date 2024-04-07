package com.jessicaarf.springbootapiproducts.config;

import com.jessicaarf.springbootapiproducts.models.RoleModel;
import com.jessicaarf.springbootapiproducts.models.UserModel;
import com.jessicaarf.springbootapiproducts.repositories.RoleRepository;
import com.jessicaarf.springbootapiproducts.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Configuration
public class AdminUserConfig implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception{

       RoleModel roleAdmin = roleRepository.findByName(RoleModel.Values.ADMIN.name());

        Optional<UserModel> userAdmin = userRepository.findByUsername("admin");

        userAdmin.ifPresentOrElse(
                user -> log.info("Admin already exists."),
                () -> {
                    var user = new UserModel();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode("admin"));
                    user.setRoles(Set.of(roleAdmin));
                    user.setActive(true);
                    userRepository.save(user);
                }
        );

    }
}
