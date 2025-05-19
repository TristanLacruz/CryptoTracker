package com.tracker.backend.mvc.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/api/admin/dashboard")
	public String getAdminDashboard() {
		return "🎉 Bienvenido al panel de administración, ADMIN!";
	}

	@GetMapping("/dashboard")
	public ResponseEntity<String> adminDashboard() {
		return ResponseEntity.ok("Bienvenido al panel de administración 👑");
	}

	@GetMapping("/status")
	public ResponseEntity<String> status() {
		return ResponseEntity.ok("🔐 El sistema está protegido y funcionando correctamente.");
	}
}
