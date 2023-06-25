package com.touramigo.service.controller;

import com.touramigo.service.model.note.NoteCreateModel;
import com.touramigo.service.model.note.NoteUpdateModel;
import com.touramigo.service.model.note.NoteViewModel;
import com.touramigo.service.service.NoteService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public List<NoteViewModel> findAllNotes(@RequestParam String type, @RequestParam UUID id) {
        return noteService.findAllNotes(type, id);
    }

    @GetMapping("/{id}")
    public NoteViewModel getById(@PathVariable UUID id) {
        return noteService.getById(id);
    }

    @PutMapping("/{id}")
    public NoteViewModel update(@PathVariable UUID id,
                                @RequestBody @Valid NoteUpdateModel model) {
        return noteService.update(id, model);
    }

    @PostMapping
    public NoteViewModel create(@RequestBody @Valid NoteCreateModel model) {
        return noteService.create(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        noteService.delete(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void bulkDelete(@RequestParam Set<UUID> ids) {
        noteService.delete(ids);
    }
}
