package com.AK.unilog.repository;

import com.AK.unilog.entity.CartItem;
import com.AK.unilog.entity.Section;
import com.AK.unilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findBySectionAndStudent(Section section, User student);
}
