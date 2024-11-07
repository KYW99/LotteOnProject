package com.lotteon.controller;
import com.lotteon.dto.adminQnaDTO;
import com.lotteon.dto.page.PageRequestDTO;
import com.lotteon.dto.page.QnaPageResponseDTO;
import com.lotteon.service.admin.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class BoardAPIController {


    private final QnaService qnaService;

    @ResponseBody
    @GetMapping("/api/qna/list/page")
    public ResponseEntity<?> adminQnaListPage(@RequestParam(required = false) Long childId, @RequestParam(required = false) Long parentId, @RequestParam(required = false) String qnawriter, PageRequestDTO pageRequestDTO) {
        pageRequestDTO.setParentId(parentId);
        pageRequestDTO.setChildId(childId);
        pageRequestDTO.setQnawriter(qnawriter);

        QnaPageResponseDTO qnaPageResponseDTO = qnaService.selectQnaListAll(pageRequestDTO);

        // qnadtoList에서 writer 아이디 마스킹
        for (adminQnaDTO qnaDTO : qnaPageResponseDTO.getQnadtoList()) {
            if (qnaDTO.getQnawriter() != null) {
                qnaDTO.setQnawriter(maskUsername(qnaDTO.getQnawriter())); // writer 아이디 마스킹 처리
            }
        }
        return ResponseEntity.ok(qnaPageResponseDTO);

    }
    // 아이디 마스킹 메소드
    public String maskUsername(String username) {
        if (username.length() <= 3) {
            return username; // 아이디가 3자 이하일 경우 그대로 반환
        }
        // 앞의 3자는 그대로 두고 나머지는 마스킹 처리
        return username.substring(0, 3) + "****";
    }

}