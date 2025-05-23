package com.project.growfit.domain.board.dto.request;

import com.project.growfit.domain.board.entity.Age;
import com.project.growfit.domain.board.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Schema(description = "제목", type = "String")
    private String title;

    @NotNull(message = "게시판을 선택해주세요.")
    @Schema(description = "게시판", type = "Category")
    private Category category;

    @NotEmpty(message = "연령대를 선택해주세요. ex) [\"TODDLER\", \"ELEMENTARY_LOW\"]")
    @Schema(description = "연령대 목록", type = "Age")
    private List<Age> ages;

    @NotBlank(message = "내용을 작성해주세요.")
    @Schema(description = "내용", type = "String")
    private String content;
}
