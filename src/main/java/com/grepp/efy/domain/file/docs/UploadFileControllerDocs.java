package com.grepp.efy.domain.file.docs;

import com.grepp.efy.domain.file.UploadFileVisibility;
import com.grepp.efy.domain.file.dto.FileUploadResponse;
import com.grepp.efy.global.exception.ErrorResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "파일", description = "공통 파일 업로드 API")
public interface UploadFileControllerDocs {

    @Operation(summary = "파일 업로드 (여러 개)",
            description = "multipart/form-data로 files(여러 개)와 visibility를 보냅니다. 한 번에 최대 10개, 파일 1개당 5MB까지 올릴 수 있습니다. "
                    + "같이 올린 파일은 모두 같은 공개 범위를 갖고, 하나라도 실패하면 전부 실패합니다(이미 올린 파일은 삭제). "
                    + "PUBLIC은 고정 URL, PRIVATE은 1시간 동안 유효한 URL을 반환합니다.")
    @SecurityRequirement(name = "accessToken")
    @ApiResponse(responseCode = "201", description = "업로드 성공, 올린 순서대로 파일 목록 반환",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FileUploadResponse.class))))
    @ApiResponse(responseCode = "400", description = "파일 없음·빈 파일, 개수 초과, visibility 값 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "빈 파일", value = """
                            {
                              "message": "빈 파일은 업로드할 수 없습니다."
                            }
                            """),
                    @ExampleObject(name = "개수 초과", value = """
                            {
                              "message": "파일은 한 번에 10개까지 업로드할 수 있습니다."
                            }
                            """),
                    @ExampleObject(name = "visibility 값 오류", value = """
                            {
                              "message": "경로 또는 쿼리 값의 타입이 올바르지 않습니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @ApiResponse(responseCode = "413", description = "파일 1개 5MB 또는 요청 전체 51MB 초과",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "크기 초과", value = """
                            {
                              "message": "파일 크기가 너무 큽니다."
                            }
                            """)
            }))
    @ApiResponse(responseCode = "500", description = "Supabase 업로드 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = {
                    @ExampleObject(name = "업로드 실패", value = """
                            {
                              "message": "파일 업로드에 실패했습니다."
                            }
                            """)
            }))
    ResponseEntity<List<FileUploadResponse>> uploadFiles(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "업로드할 파일들 (같은 key로 여러 번 보냄)") List<MultipartFile> files,
            @Parameter(description = "공개 범위", required = true, example = "PUBLIC") UploadFileVisibility visibility);
}
