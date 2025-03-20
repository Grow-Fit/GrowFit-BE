package com.project.growfit.domain.auth.repository;

import com.project.growfit.domain.auth.entity.Parent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ParentRepositoryTest {

    @Autowired
    private ParentRepository parentRepository;


    @Test
    @DisplayName("[FindByEmail 성공 테스트] 존재하는 Parent 엔티티 반환")
    void testFindByEmail() {
        Parent parent = new Parent();

        ReflectionTestUtils.setField(parent, "email", "test@example.com");
        ReflectionTestUtils.setField(parent, "providerId", "kakao123");

        parentRepository.save(parent);

        Optional<Parent> result = parentRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent(), "Parent 엔티티가 존재해야 합니다.");
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("[findParentByProviderId 성공 테스트] providerId로 Parent 목록 반환")
    void testFindParentByProviderId() {
        Parent parent1 = new Parent();
        ReflectionTestUtils.setField(parent1, "email", "a@example.com");
        ReflectionTestUtils.setField(parent1, "providerId", "kakao123");
        parentRepository.save(parent1);

        Parent parent2 = new Parent();
        ReflectionTestUtils.setField(parent2, "email", "test@example.com");
        ReflectionTestUtils.setField(parent2, "providerId", "kakao123");
        parentRepository.save(parent2);

        Optional<List<Parent>> resultOpt = parentRepository.findParentByProviderId("kakao123");

        assertTrue(resultOpt.isPresent(), "ProviderId에 해당하는 Parent 목록이 존재해야 합니다.");
        List<Parent> parents = resultOpt.get();
        assertEquals(2, parents.size(), "두 개의 Parent 엔티티가 조회되어야 합니다.");
    }
}
