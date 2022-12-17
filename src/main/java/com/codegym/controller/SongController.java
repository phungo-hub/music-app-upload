package com.codegym.controller;

import com.codegym.model.Song;
import com.codegym.model.SongForm;
import com.codegym.service.ISongService;
import com.codegym.service.SongService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/songs")
@PropertySource("classpath:upload_file.properties")
public class SongController {
    @Value("${file-upload}")
    private String fileUpload;
    private final ISongService songService = new SongService();
    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("/index", "songs", songService.findAll());
    }
    @GetMapping("/create")
    public ModelAndView create() {
        return new ModelAndView("/create","songForm", new SongForm());
    }

    @PostMapping("/save")
    public ModelAndView saveProduct(@ModelAttribute SongForm songForm) {
        MultipartFile multipartFile = songForm.getFile();
        String fileName = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(songForm.getFile().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Song song = new Song(songForm.getId(), songForm.getName(), songForm.getArtist(),
                            songForm.getGenre(), fileName);
        songService.save(song);
        return new ModelAndView("/create", "songForm", songForm);
    }
}
