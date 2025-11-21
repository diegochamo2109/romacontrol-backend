

    package com.romacontrol.romacontrol_v1.dto.rol;

    import java.time.OffsetDateTime;
    import java.util.List;

    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class RolDetalleRespuesta {

        private Long id;
        private String nombre;
        private String descripcion;
        private OffsetDateTime fechaCreacion;
        private boolean activo;
        private List<PermisoRolRespuesta> permisos;
        
    }
