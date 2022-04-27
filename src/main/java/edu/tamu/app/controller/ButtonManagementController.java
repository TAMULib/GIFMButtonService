package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.PersistedButton;
import edu.tamu.app.model.repo.PersistedButtonRepo;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

@RestController
@RequestMapping("/button-management")
public class ButtonManagementController {

    @Autowired
    PersistedButtonRepo persistedButtonRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getAll() {
        return new ApiResponse(SUCCESS, persistedButtonRepo.findAll(Sort.by(Sort.Direction.ASC, "name")));
    }

    @RequestMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse create(@RequestBody PersistedButton button) {
        return new ApiResponse(SUCCESS, persistedButtonRepo.create(button));
    }

    @RequestMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse getButton(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, persistedButtonRepo.findById(id).get());
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse update(@WeaverValidatedModel PersistedButton button) {
        return new ApiResponse(SUCCESS, persistedButtonRepo.update(button));
    }

    @RequestMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse remove(@WeaverValidatedModel PersistedButton button) {
        persistedButtonRepo.delete(button);
        return new ApiResponse(SUCCESS);
    }

}
