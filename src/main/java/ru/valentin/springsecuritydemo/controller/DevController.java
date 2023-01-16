package ru.valentin.springsecuritydemo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;
import ru.valentin.springsecuritydemo.model.Developer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api/v1/developers")
public class DevController {
  private List<Developer> DEVELOPERS =
      Stream.of(
              new Developer(1L, "Valya", "Zhur"),
              new Developer(2L, "Alex", "Gorshkov"),
              new Developer(3L, "Alex", "Kruglov"))
          .collect(Collectors.toList());

  @GetMapping
  @PreAuthorize("hasAuthority('developers:read')")
  public List<Developer> getAll() {
    return DEVELOPERS;
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('developers:read')")
  public Developer getById(@PathVariable Long id) {
    return DEVELOPERS.stream()
        .filter(dev -> Objects.equals(id, dev.getId()))
        .findFirst()
        .orElse(null);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('developers:write')")
  public void deleteById(@PathVariable Long id) {
    DEVELOPERS.removeIf(dev -> Objects.equals(id, dev.getId()));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('developers:write')")
  public Developer create(@RequestBody Developer developer) {
    DEVELOPERS.add(developer);
    return developer;
  }
}
