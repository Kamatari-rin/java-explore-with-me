package ru.practicum.main.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.dto.category.NewCategoryDto;
import ru.practicum.main.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Validated
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Create category {}", newCategoryDto);
        return new ResponseEntity<>(categoryService.create(newCategoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Boolean> delete(@PathVariable(value = "catId") Long catId) {
        log.info("Delete category with id= {}", catId);
        return new ResponseEntity<>(categoryService.delete(catId), HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> update(@PathVariable(value = "catId") Long catId,
                                              @RequestBody @Valid CategoryDto dto) {
        log.info("Update category {} with id= {}", dto, catId);
        return new ResponseEntity<>(categoryService.update(catId, dto), HttpStatus.OK);
    }
}
