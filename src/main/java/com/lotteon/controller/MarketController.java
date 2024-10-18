package com.lotteon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Log4j2
@RequiredArgsConstructor
@Controller

@RequestMapping("/market")
public class MarketController {

    @GetMapping("/{content}")
    public String index(@PathVariable String content, Model model) {

        model.addAttribute("content", content);
        return "marketindex";
    }

}
