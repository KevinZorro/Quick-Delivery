package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.Rol;
import lombok.Data;
import java.util.Map;

@Data
public class UsuarioDto {
    private String nombre;
    private String contraseña;
    private String correo;
    private String telefono;
    private Rol rol;
    private String fotoPerfil;

    private Map<String, Object> detalles;
}
