  package com.romacontrol.romacontrol_v1.web;

  import java.util.List;

  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RequestParam;
  import org.springframework.web.bind.annotation.RestController;

  import com.romacontrol.romacontrol_v1.model.Genero;
  import com.romacontrol.romacontrol_v1.model.Localidad;
  import com.romacontrol.romacontrol_v1.model.Provincia;
  import com.romacontrol.romacontrol_v1.model.Rol;
  import com.romacontrol.romacontrol_v1.model.TipoCuota;
  import com.romacontrol.romacontrol_v1.repository.GeneroRepository;
  import com.romacontrol.romacontrol_v1.repository.LocalidadRepository;
  import com.romacontrol.romacontrol_v1.repository.ProvinciaRepository;
  import com.romacontrol.romacontrol_v1.repository.RolRepository;
  import com.romacontrol.romacontrol_v1.repository.TipoCuotaRepository;

  import lombok.RequiredArgsConstructor;

  @RestController
  @RequestMapping("/api/catalogos")
  @RequiredArgsConstructor
  public class CatalogoController {
    private final GeneroRepository generoRepo;
    private final RolRepository rolRepo;
    private final TipoCuotaRepository tipoCuotaRepo;
    private final ProvinciaRepository provinciaRepo;
    private final LocalidadRepository localidadRepo;

    @GetMapping("/generos")       public List<Genero> generos() { return generoRepo.findAll(); }
    @GetMapping("/roles")         public List<Rol> roles() { return rolRepo.findAll(); }
    @GetMapping("/tipos-cuota")   public List<TipoCuota> tiposCuota() { return tipoCuotaRepo.findAll(); }
    @GetMapping("/provincias")    public List<Provincia> provincias() { return provinciaRepo.findAll(); }
    @GetMapping("/localidades")
    public List<Localidad> localidades(@RequestParam Long provinciaId) {
      return localidadRepo.findByProvinciaId(provinciaId);
    }
  }
