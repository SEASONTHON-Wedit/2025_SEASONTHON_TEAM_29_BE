package com.wedit.backend.api.vendor.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wedit.backend.api.vendor.service.VendorService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v3/vendor")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Vendor", description = "Vendor 관련 API 입니다.")
public class VendorController {

	private final VendorService vendorService;

}
