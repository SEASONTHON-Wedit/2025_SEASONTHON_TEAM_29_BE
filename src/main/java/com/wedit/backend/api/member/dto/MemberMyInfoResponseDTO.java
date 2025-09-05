package com.wedit.backend.api.member.dto;

import com.wedit.backend.api.member.entity.Couple;
import com.wedit.backend.api.member.entity.Member;
import com.wedit.backend.api.member.entity.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
public class MemberMyInfoResponseDTO {

    private String name;        // 이름
    private String type;        // GROOM, BRIDE
    private Long weddingDday;   // Dday
    private boolean isCoupled;
    private String partnerName;

    public static MemberMyInfoResponseDTO from(Member member) {
        MemberMyInfoResponseDTO dto = new MemberMyInfoResponseDTO();
        dto.name = member.getName();
        dto.type = member.getType().toString();
        dto.weddingDday = calculateDday(member.getWeddingDate());

        Couple couple = (member.getType() == Type.GROOM) ? member.getAsGroom() : member.getAsBride();

        if (couple != null && couple.getGroom() != null && couple.getBride() != null) {
            dto.isCoupled = true;
            dto.partnerName = (member.getType() == Type.GROOM) ? couple.getBride().getName() : couple.getGroom().getName();
        } else {
            dto.isCoupled = false;
            dto.partnerName = null;
        }

        return dto;
    }

    private static Long calculateDday(LocalDate weddingDate) {
        if (weddingDate == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), weddingDate);
    }
}
