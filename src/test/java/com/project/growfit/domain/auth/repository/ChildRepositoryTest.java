package com.project.growfit.domain.auth.repository;

import com.project.growfit.domain.auth.entity.Child;
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
        Child child = new Child();
        ReflectionTestUtils.setField(child, "childId", "child123");
        ReflectionTestUtils.setField(child, "code", "codeABC");
        childRepository.save(child);

        // When: childId로 조회
        Optional<Child> result = childRepository.findByChildId("child123");

        // Then: 결과 검증
        assertTrue(result.isPresent(), "Child 엔티티가 존재해야 합니다.");
        assertEquals("child123", result.get().getChildId());
    }

    @Test
    @DisplayName("[existsByCodeAndChildId 성공 테스트] 해당 code와 childId 조합의 존재 여부 확인")
    void testExistsByCodeAndChildId() {
        Child child = new Child();

        ReflectionTestUtils.setField(child, "childId", "child456");
        ReflectionTestUtils.setField(child, "code", "codeXYZ");
        childRepository.save(child);

        boolean exists = childRepository.existsByCodeAndChildId("codeXYZ", "child456");

        assertTrue(exists, "해당 code와 childId 조합이 존재해야 합니다.");
    }

    @Test
    @DisplayName("[findByCode 성공 테스트] code로 Child 엔티티 조회")
    void testFindByCode() {
        Child child = new Child();
        ReflectionTestUtils.setField(child, "childId", "child321");
        ReflectionTestUtils.setField(child, "code", "codeOPQ");
        childRepository.save(child);

        Optional<Child> result = childRepository.findByCode("codeOPQ");

        assertTrue(result.isPresent(), "Child 엔티티가 존재해야 합니다.");
        assertEquals("codeOPQ", ReflectionTestUtils.getField(result.get(), "code"));
    }

}
