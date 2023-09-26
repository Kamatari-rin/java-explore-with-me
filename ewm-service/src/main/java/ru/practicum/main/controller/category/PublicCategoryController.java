package ru.practicum.main.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.dto.category.CategoryDto;
import ru.practicum.main.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.constant.Constants.PAGE_INDEX_FROM;
import static ru.practicum.constant.Constants.PAGE_INDEX_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Validated
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> get(@RequestParam(defaultValue = PAGE_INDEX_FROM) @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = PAGE_INDEX_SIZE) @Positive Integer size) {
        log.info("Get category, parameters: from= {} size= {}", from, size);
        return new ResponseEntity<>(categoryService.get(from, size), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long catId) {
        log.info("Get category with id {}", catId);
        return new ResponseEntity<>(categoryService.getById(catId), HttpStatus.OK);
    }
}
