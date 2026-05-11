package com.ufps.Quick_Delivery.dto;

import com.ufps.Quick_Delivery.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class UsuarioDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contraseña;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    private String correo;

    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    private String fotoPerfil;

    private Map<String, Object> detalles;
}
