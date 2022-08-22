package com.there.src.artistStatement;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.artistStatement.model.GetArtistStatementRes;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/statements")
public class ArtistStatementController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtistStatementProvider artistStatementProvider;
    private final ArtistStatementService artistStatementService;
    private final JwtService jwtService;


    /**
     * 작가노트 조회 API
     * [GET] /statements/users/:userIdx
     */
    @ApiOperation(value="작가노트 조회 API", notes="유저의 작가노트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    @ResponseBody
    @GetMapping("/users/{userIdx}")
    public BaseResponse<GetArtistStatementRes> getArtistStatement(@PathVariable("userIdx") int userIdx) {
        try {

            GetArtistStatementRes getArtistStatementRes = artistStatementProvider.retrieveStatement(userIdx);
            return new BaseResponse<>(getArtistStatementRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }




}

