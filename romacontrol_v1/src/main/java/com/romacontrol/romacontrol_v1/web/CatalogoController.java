package com.romacontrol.romacontrol_v1.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.romacontrol.romacontrol_v1.dto.CatalogoItemResponse;
import com.romacontrol.romacontrol_v1.model.CuotaMensual;
import com.romacontrol.romacontrol_v1.repository.CuotaMensualRepository;
import com.romacontrol.romacontrol_v1.repository.EstadoCuotaRepository;
import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
import com.romacontrol.romacontrol_v1.repository.ProvinciaRepository;
import com.romacontrol.romacontrol_v1.repository.RolRepository;
import com.romacontrol.romacontrol_v1.repository.TipoCuotaRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true")
public class CatalogoController {

 private final EstadoCuotaRepository estadoCuotaRepo;
    private final TipoCuotaRepository tipoCuotaRepo;
    private final GeneroRepository generoRepo;
    private final ProvinciaRepository provinciaRepo;
    private final LocalidadRepository localidadRepo;
    private final RolRepository rolRepo;
    private final CuotaMensualRepository cuotaRepo;

    @GetMapping("/generos")
    @PreAuthorize("isAuthenticated()")
    public List<CatalogoItemResponse> generos() {
        return generoRepo.findAll()
                .stream()
                .map(g -> new CatalogoItemResponse(g.getId(), g.getNombre()))
                .toList();
    }

    @GetMapping("/provincias")
    @PreAuthorize("isAuthenticated()")
    public List<CatalogoItemResponse> provincias() {
        return provinciaRepo.findAllByOrderByNombreAsc()
                .stream()
                .map(p -> new CatalogoItemResponse(p.getId(), p.getNombre()))
                .toList();
    }

    @GetMapping("/localidades")
    @PreAuthorize("isAuthenticated()")
    public List<CatalogoItemResponse> localidadesPorProvincia(@RequestParam Long provinciaId) {
        return localidadRepo.findAllByProvinciaIdOrderByNombreAsc(provinciaId)
                .stream()
                .map(l -> new CatalogoItemResponse(l.getId(), l.getNombre()))
                .toList();
    }

    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    public List<CatalogoItemResponse> roles() {
        return rolRepo.findAllByOrderByNombreAsc()
                .stream()
                .map(r -> new CatalogoItemResponse(r.getId(), r.getNombre()))
                .toList();
    }

    @GetMapping("/cuotas-activas")
@PreAuthorize("isAuthenticated()")
public List<CatalogoItemResponse> cuotasActivas() {
  return cuotaRepo.findAll().stream()
      .filter(CuotaMensual::isActiva)
      .map(c -> new CatalogoItemResponse(c.getId(), c.getDescripcion()))
      .toList();
}


    @GetMapping("/estados-cuota")
    @PreAuthorize("isAuthenticated()")
    public List<CatalogoItemResponse> estadosCuota() {
        return estadoCuotaRepo.findAllByOrderByNombreAsc()
                .stream()
                .map(e -> new CatalogoItemResponse(e.getId(), e.getNombre()))
                .toList();
    }

    @GetMapping("/tipos-cuota")
    @PreAuthorize("isAuthenticated()")
    public List<CatalogoItemResponse> tiposCuota() {
        return tipoCuotaRepo.findAllByOrderByNombreAsc()
                .stream()
                .map(t -> new CatalogoItemResponse(t.getId(), t.getNombre()))
                .toList();
    }
}
