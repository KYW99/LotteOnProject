package com.lotteon.controller;
/*
   날짜: 2024/10/25
   이름 : 최영진
   내용 : 쿠폰pagine

   추가내역
   -------------
   10.26  하진희 - 추가작업내역 작성
 */

import com.lotteon.dto.admin.CouponDTO;
import com.lotteon.dto.admin.CouponIssuedDTO;
import com.lotteon.dto.admin.CouponListRequestDTO;
import com.lotteon.dto.admin.CouponListResponseDTO;
import com.lotteon.dto.product.ProductDTO;
import com.lotteon.entity.User.Member;
import com.lotteon.entity.User.Seller;
import com.lotteon.entity.admin.Coupon;
import com.lotteon.entity.admin.CouponIssued;
import com.lotteon.entity.product.Product;
import com.lotteon.repository.admin.CouponRepository;
import com.lotteon.repository.user.MemberRepository;
import com.lotteon.security.MyUserDetails;
import com.lotteon.service.admin.CouponIssuedService;
import com.lotteon.service.admin.CouponService;
import com.lotteon.service.user.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/seller/coupon")
public class AdminCouponController {

    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final CouponIssuedService couponIssuedService;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    ;

    @GetMapping("/list")
    public String adminCouponList(
            @ModelAttribute CouponListRequestDTO requestDTO,
            Model model) {
        if (requestDTO.getPage() < 1) {
            requestDTO.setPage(1);
        }
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        Seller seller = userDetails.getSeller();

        List<String> userRoles = couponService.getUserRoles();

        Pageable pageable = requestDTO.getPageable(); // 정렬 기준 없이 호출

        model.addAttribute("seller", seller); // 셀러 정보를 모델에 추가

        Page<CouponDTO> couponPage = couponService.selectCouponsPagination(requestDTO, seller.getId(), pageable);


        log.info("페이징: {}", pageable); // Pageable 로그 추가

        // CouponListResponseDTO 생성
        CouponListResponseDTO responseDTO = CouponListResponseDTO.builder()
                .couponDTOList(couponPage.getContent())
                .total((int) couponPage.getTotalElements())
                .pg(requestDTO.getPage()) // 요청 DTO에서 현재 페이지 번호 가져오기
                .size(requestDTO.getSize()) // 요청 DTO에서 페이지 크기 가져오기
                .build();


        // 페이지 정보 계산
        responseDTO.setStartNo(responseDTO.getTotal() - ((responseDTO.getPg() - 1) * responseDTO.getSize()));
        responseDTO.setStart(Math.max(1, responseDTO.getEnd() - (responseDTO.getSize() - 1)));
        responseDTO.setEnd(Math.min((int) (Math.ceil(responseDTO.getPg() / (double) responseDTO.getSize()) * responseDTO.getSize()), responseDTO.getTotal()));
        responseDTO.setPrev(responseDTO.getStart() > 1);
        responseDTO.setNext(responseDTO.getTotal() > responseDTO.getEnd());

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("couponList", couponPage.getContent());
        model.addAttribute("totalPages", couponPage.getTotalPages());
        model.addAttribute("currentPage", couponPage.getNumber());

        return "content/admin/coupon/list";
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerCoupon(@RequestBody CouponDTO couponDTO) {

        log.info("쿠폰 디티어"+ couponDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        Seller seller = userDetails.getSeller();

        try {
            couponService.insertCoupon(couponDTO);
            return ResponseEntity.ok("쿠폰이 등록되었습니다.");
        } catch (Exception e) {
            log.error("Coupon registration failed--------------------: {}", e.getMessage());
            return ResponseEntity.status(500).body("등록에 실패했습니다: " + e.getMessage());
        }
    }

    @PutMapping("/{couponId}/end")
    public ResponseEntity<CouponDTO> endCoupon(@PathVariable("couponId") String couponId) {
        CouponDTO updatedCoupon = couponService.endCoupon(couponId);

        return ResponseEntity.ok(updatedCoupon);

    }
    @PutMapping("/issued/{issuanceNumber}/end")
    public ResponseEntity<CouponIssuedDTO> endIssuedCoupon(@PathVariable("issuanceNumber") String issuanceNumber) {
        CouponIssuedDTO updatedCoupon = couponIssuedService.endCouponIssued(issuanceNumber);
        return ResponseEntity.ok(updatedCoupon);
    }

    @GetMapping("/issued")
    public String adminIssuedModify(CouponListRequestDTO requestDTO, Model model) {
        log.info("쿠폰 발급 목록을 요청했습니다.");

        String sellerCompany = couponIssuedService.getLoggedInSellerCompany(); // 셀러 회사명을 가져오는 로직 필요
        Pageable pageable = PageRequest.of(requestDTO.getPage() - 1, requestDTO.getSize());

        Page<CouponIssued> issuedPage = couponIssuedService.selectIssuedCouponsPagination(requestDTO, sellerCompany, pageable);

        // 셀러 이름을 이용해 발급된 쿠폰 목록 조회
        // 셀러 이름을 이용해 발급된 쿠폰 목록 조회 (엔티티를 직접 조회)
        if (issuedPage == null || issuedPage.getContent().isEmpty()) {
            log.info("발급된 쿠폰 목록이 없습니다.");
        } else {
            log.info("잇슈드페이지: {}", issuedPage);
        }
        // 모델에 발급된 쿠폰 리스트 담기
        model.addAttribute("IssuedList", issuedPage.getContent());  // 실제 발급된 쿠폰 리스트
        model.addAttribute("totalPages", issuedPage.getTotalPages());  // 전체 페이지 수
        model.addAttribute("currentPage", issuedPage.getNumber() + 1); // 현재 페이지 (1-based)
        model.addAttribute("totalItems", issuedPage.getTotalElements());  // 전체 아이템 수
        model.addAttribute("size", issuedPage.getSize());  // 페이지당 아이템 수
//        model.addAttribute("IssuedList", IssuedList);  // 페이지당 아이템 수

        return "content/admin/coupon/issued";
    }



    @GetMapping("/{productId}")
    public ResponseEntity<List<CouponDTO>> getCouponsForProduct(@PathVariable Long productId) {
        List<Coupon> coupons = couponService.selectCouponIssued(productId);
        // Coupon 리스트를 CouponDTO 리스트로 변환
        List<CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> modelMapper.map(coupon, CouponDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(couponDTOs);
    }

    @GetMapping("/all/coupons")
    public ResponseEntity<List<CouponDTO>> getAllCoupons() {
        List<Coupon> coupons = couponService.selectCouponIssued(null);
        // Coupon 리스트를 CouponDTO 리스트로 변환
        List<CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> modelMapper.map(coupon, CouponDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(couponDTOs);
    }

    @GetMapping(value = "/products", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> getProductsForCoupons() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserId = authentication.getName(); // 로그인한 사용자 ID


        // 로그인한 사용자 ID로 상품 조회
        List<ProductDTO> products = couponService.getProductsBySellerId(loggedInUserId);
        // 셀러 ID와 상품명만 담는 리스트 생성
        List<Map<String, Object>> productInfoList = products.stream()
                .map(product -> {
                    Map<String, Object> productInfo = new HashMap<>();
                    productInfo.put("sellerId", product.getSellerId());
                    productInfo.put("productId", product.getProductId());
                    productInfo.put("productName", product.getProductName());
                    return productInfo;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(productInfoList);

    }

    @PostMapping("/apply/{couponId}")
    public ResponseEntity<CouponDTO> applyCoupon(@PathVariable("couponId") String couponId, Principal principal) {
        log.info("쿠폰 발금 버튼 클릭되서 요청왔다");

        String userUid = principal.getName();

        Member member = memberRepository.findByUser_Uid(userUid)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        log.info("사용자 가 누군가"+ member);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다."));
        log.info("어떤 쿠폰인가 "+coupon);
        Product product = coupon.getProduct();

        couponIssuedService.insertCouponIssued(member, coupon, product);

        couponIssuedService.useCoupon(couponId);


        CouponDTO couponDTO = new CouponDTO();
        couponDTO.setCouponId(coupon.getCouponId());
        couponDTO.setCouponName(coupon.getCouponName());
        couponDTO.setCouponType(coupon.getCouponType());

        return ResponseEntity.ok(couponDTO);
    }






}





