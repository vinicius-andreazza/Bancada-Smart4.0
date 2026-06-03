package com.smart.appsa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.appsa.dto.PedidoRequestDTO;


@Controller
@RequestMapping("")
public class PageController {
    
    @GetMapping("/dashboard")
    public String getDashboard() {
        return "dashboard";
    }

    @GetMapping("/pedidos/novo")
    public String getPedidoNovo(Model model) {
        model.addAttribute("pedido", PedidoRequestDTO.builder().build());
        return "pedidos/form";
    }
    
}
