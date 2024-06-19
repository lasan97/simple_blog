package io.martin.inside.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @since       2024.06.19
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@RestController
@RequiredArgsConstructor
public class PageController {

	@GetMapping("/xss")
	public String getXss(@RequestParam("content") String content) {
		return content;
	}

	@Data
	public static class Xss {

		private String content;
	}

	@PostMapping("/xss")
	public String addXss(@RequestBody Xss xss) {
		return xss.content;
	}
}
