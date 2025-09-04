package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.util.JsonFormatPattern;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;


@RestController
public interface StatsInterface {

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    List<StatsResponseDto> getStats(@DateTimeFormat(pattern = JsonFormatPattern.TIME_PATTERN) @RequestParam(value = "start") LocalDateTime start,
                                    @DateTimeFormat(pattern = JsonFormatPattern.TIME_PATTERN) @RequestParam(value = "end") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") Boolean unique);

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    StatsRequestDto save(@RequestBody @Valid StatsRequestDto statsRequestDto);
}
