package it.progetto.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rosar")
public class HelloController
{
    @GetMapping("saluto")
    public String ciao()
    {
        return "Hello World";
    }

    @GetMapping("numero")
    public int getNumero()
    {
        return 1;
    }

    @GetMapping("numero/{input}")
    public int getNumero(@PathVariable String input)
    {
        return Integer.parseInt(input);
    }
}
