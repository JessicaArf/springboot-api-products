package com.jessicaarf.springbootapiproducts.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_roles")
@Getter
@Setter
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    private String name;

    public enum Values {
        admin(1L),
        basic(2L);

        long roleId;

        Values(long roleId) {
            this.roleId = roleId;
        }

    }
}
