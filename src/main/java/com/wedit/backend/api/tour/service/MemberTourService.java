package com.wedit.backend.api.tour.service;

import org.springframework.stereotype.Service;

import com.wedit.backend.api.tour.repository.MemberTourRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberTourService {
	private final MemberTourRepository memberTourRepository;



}
