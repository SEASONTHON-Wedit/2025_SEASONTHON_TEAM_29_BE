package com.wedit.backend.api.vendor.service;

import org.springframework.stereotype.Service;

import com.wedit.backend.api.vendor.repository.VendorImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorImageService {
	private final VendorImageRepository vendorImageRepository;


}
