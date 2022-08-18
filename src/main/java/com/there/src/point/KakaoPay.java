package com.there.src.point;

import com.there.src.point.model.*;
import lombok.extern.java.Log;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.Integer.parseInt;


@Service
@Log
public class KakaoPay {

    private static final String HOST = "https://kapi.kakao.com";

    private KakaoPayReadyVO kakaoPayReadyVO;
    private KakaoPayApprovalVO kakaoPayApprovalVO;

    private final PointDao pointDao;
    private GetPointCancleRes getPointCancleRes;

    public KakaoPay(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    public String kakaoPayReady(int userIdx, PostPointReq postpointReq) {

        RestTemplate restTemplate = new RestTemplate();

        // 서버로 요청할 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + "89f7949d98d25443bb89a05c88266529");
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        // 서버로 요청할 Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", "there.com");
        params.add("partner_user_id", Integer.toString(userIdx));
        params.add("item_name", "머니충전");
        params.add("quantity", "1");
        params.add("total_amount",Integer.toString(postpointReq.getAmount()));
        params.add("tax_free_amount", Integer.toString(postpointReq.getAmount()/10));
        params.add("approval_url", "https://recordinthere.shop/kakaoPaySuccess/"+userIdx);
        params.add("cancel_url", "https://recordinthere.shop/kakaoPayCancel");
        params.add("fail_url", "https://recordinthere.shop/kakaoPaySuccessFail");

        System.out.println(userIdx);

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

        try {
            kakaoPayReadyVO = restTemplate.postForObject(new URI(HOST + "/v1/payment/ready"), body, KakaoPayReadyVO.class);

            log.info("" + kakaoPayReadyVO);


            System.out.println("" + kakaoPayReadyVO.getNext_redirect_pc_url());
            return kakaoPayReadyVO.getNext_redirect_pc_url();

        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "/pay";

    }

    public KakaoPayApprovalVO kakaoPayInfo(String pg_token, int userIdx) {

        log.info("KakaoPayInfoVO............................................");
        log.info("-----------------------------");
        System.out.println(kakaoPayReadyVO.getTid());

        RestTemplate restTemplate = new RestTemplate();

        // 서버로 요청할 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + "89f7949d98d25443bb89a05c88266529");
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        // 서버로 요청할 Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", kakaoPayReadyVO.getTid());
        params.add("partner_order_id", "there.com");
        params.add("partner_user_id", String.valueOf(userIdx));
        params.add("pg_token", pg_token);



        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

        try {
            kakaoPayApprovalVO = restTemplate.postForObject(new URI(HOST + "/v1/payment/approve"), body, KakaoPayApprovalVO.class);
            log.info("" + kakaoPayApprovalVO);


            Integer Idx = parseInt(kakaoPayApprovalVO.getPartner_user_id());
            Integer amount = kakaoPayApprovalVO.getAmount().getTotal();
            String tid = kakaoPayApprovalVO.getTid();
            Integer tax_free_amount = kakaoPayApprovalVO.getAmount().getTax_free();

            pointDao.chargePoint(Idx, amount,tax_free_amount, tid);

            System.out.println("결제 성공하였습니다. ");

            return kakaoPayApprovalVO;

        } catch (RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public GetPointCancleRes kakaoPayCancle(int pointIdx){
        RestTemplate restTemplate = new RestTemplate();

        // 서버로 요청할 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + "89f7949d98d25443bb89a05c88266529");
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");

        GetPointRes getPointRes = pointDao.selectPoint(pointIdx);

        // 서버로 요청할 Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid","TC0ONETIME");
        params.add("tid", getPointRes.getTid());
        params.add("cancel_amount", String.valueOf(getPointRes.getCancle_amount()));
        params.add("cancel_tax_free_amount", String.valueOf(getPointRes.getTax_free_amount()));

        HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<>(params, headers);

        try {
            getPointCancleRes = restTemplate.postForObject(new URI (HOST + "/v1/payment/cancel"), body, GetPointCancleRes.class);

            pointDao.cancelPoint(pointIdx);

            return getPointCancleRes;

        } catch(RestClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
