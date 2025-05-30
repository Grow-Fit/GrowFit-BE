package com.project.growfit.domain.User.repository;

import com.project.growfit.domain.User.entity.Child;
import com.project.growfit.domain.User.entity.ROLE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ChildRepositoryTest {

    @Autowired
    private ChildRepository childRepository;

    @Test
    @DisplayName("[findByChildId 성공 테스트] 존재하는 Child 엔티티 반환")
    void testFindByChildId() {
        Child child = new Child("child123", "1234", "테스트", ROLE.ROLE_CHILD);
        childRepository.save(child);
        // When
        Optional<Child> result = childRepository.findByLoginId("child123");
        // Then
        assertTrue(result.isPresent(), "Child 엔티티가 존재해야 합니다.");
        assertEquals("child123", result.get().getLoginId());
    }

    @Test
    @DisplayName("[existsByCodeAndChildId 성공 테스트] 해당 code와 childId 조합의 존재 여부 확인")
    void testExistsByCodeAndChildId() {
        Child child = new Child("child123", "1234", "테스트", ROLE.ROLE_CHILD);
        ReflectionTestUtils.setField(child, "codeNumber", "codeXYZ");
        childRepository.save(child);

        boolean exists = childRepository.existsByCodeNumberAndLoginId("codeXYZ", "child123");

        assertTrue(exists, "해당 code와 childId 조합이 존재해야 합니다.");
    }

    @Test
    @DisplayName("[findByCode 성공 테스트] code로 Child 엔티티 조회")
    void testFindByCode() {
        Child child = new Child("child123", "1234", "테스트", ROLE.ROLE_CHILD);
        ReflectionTestUtils.setField(child, "codeNumber", "codeXYZ");
        childRepository.save(child);

        Optional<Child> result = childRepository.findByCodeNumber("codeXYZ");

        assertTrue(result.isPresent(), "Child 엔티티가 존재해야 합니다.");
        assertEquals("codeXYZ", ReflectionTestUtils.getField(result.get(), "codeNumber"));
    }

}

