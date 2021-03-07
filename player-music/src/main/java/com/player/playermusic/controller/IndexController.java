package com.player.playermusic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ResourceLoader;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;

@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    ResourceLoader loader;

    @GetMapping("/")
    public String home() {
        return "redirect:music";
    }

    @GetMapping("/music")
    public String index() {
        return "forward:index.html";
    }

    @RequestMapping(value = {"/images/*/*/*", "/images/*/*"}, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] proxyImage(HttpServletRequest request) {
        File file = new File("E:/Node/music/public/" + request.getServletPath());
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/audio/*", produces = "audio/mp4a-latm")
    @ResponseBody
    public byte[] getFile(HttpServletRequest request) {
        String file = "E:/Node/music/public/" + request.getServletPath();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
