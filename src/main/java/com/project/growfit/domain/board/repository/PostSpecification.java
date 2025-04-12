package com.project.growfit.domain.board.repository;

import com.project.growfit.domain.board.entity.Age;
import com.project.growfit.domain.board.entity.Category;
import com.project.growfit.domain.board.entity.Like;
import com.project.growfit.domain.board.entity.Post;
import com.project.growfit.domain.board.entity.PostAge;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class PostSpecification {

    // Category 필터
    public static Specification<Post> hasCategory(Category category) {
        return ((root, query, criteriaBuilder) ->
                category == null ? null : criteriaBuilder.equal(root.get("category"), category));
    }

    // Age 필터 (여러 개 가능)
    public static Specification<Post> hasAnyAge(List<Age> ages) {
        return (root, query, criteriaBuilder) -> {
            if (ages == null || ages.isEmpty()) return null;

            Join<Post, PostAge> postAgeJoin = root.join("postAges", JoinType.INNER);

            CriteriaBuilder.In<Age> inClause = criteriaBuilder.in(postAgeJoin.get("age"));
            for (Age age : ages) {
                inClause.value(age);
            }

            return inClause;
        };
    }

    // 최신순 정렬 (기본값)
    public static Specification<Post> orderByCreatedAt() {
        return ((root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return null;
        });
    }

    // 조회수 순 정렬
    public static Specification<Post> orderByHits() {
        return ((root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("hits")));
            return null;
        });
    }

    // 좋아요 순 정렬
    public static Specification<Post> orderByLikes() {
        return ((root, query, criteriaBuilder) -> {
            Join<Post, Like> likeJoin = root.join("likes", JoinType.LEFT);
            query.groupBy(root.get("id"));
            query.orderBy(criteriaBuilder.desc(criteriaBuilder.count(likeJoin)));
            return null;
        });
    }
}
