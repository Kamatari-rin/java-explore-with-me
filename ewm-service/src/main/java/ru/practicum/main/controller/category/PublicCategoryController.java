package ru.practicum.main.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constant.Constants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Validated
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> get(@RequestParam(defaultValue = PAGE_INDEX_FROM) @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = PAGE_INDEX_SIZE) @Positive Integer size) {
        return new ResponseEntity<>(categoryService.get(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long catId) {
        return new ResponseEntity<>(categoryService.getById(catId), HttpStatus.OK);
    }
}
